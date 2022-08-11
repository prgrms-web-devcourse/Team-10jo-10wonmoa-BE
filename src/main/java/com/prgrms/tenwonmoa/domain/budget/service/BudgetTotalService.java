package com.prgrms.tenwonmoa.domain.budget.service;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.budget.dto.CreateOrUpdateBudgetRequest;
import com.prgrms.tenwonmoa.domain.budget.dto.FindBudgetByRegisterDate;
import com.prgrms.tenwonmoa.domain.budget.dto.FindBudgetData;
import com.prgrms.tenwonmoa.domain.budget.dto.FindBudgetWithExpenditureResponse;
import com.prgrms.tenwonmoa.domain.budget.repository.BudgetQueryRepository;
import com.prgrms.tenwonmoa.domain.budget.repository.BudgetRepository;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.service.UserCategoryService;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BudgetTotalService {
	private static final String INVALID_AMOUNT_EXP_MSG = "예산 금액이 0원 입니다.";
	private static final int PERCENTAGE = 100;

	private static final Long AMOUNT_MIN = 0L;
	private static final String YEAR_MONTH_SEPARATOR = "-";
	private final BudgetRepository budgetRepository;
	private final UserService userService;
	private final UserCategoryService userCategoryService;
	private final BudgetQueryRepository budgetQueryRepository;

	public void createOrUpdateBudget(Long userId, CreateOrUpdateBudgetRequest createOrUpdateBudgetRequest) {
		UserCategory userCategory = userCategoryService.findById(createOrUpdateBudgetRequest.getUserCategoryId());
		userCategory.getUser().validateLoginUser(userId);
		User authUser = userService.findById(userId);

		budgetRepository.findByUserCategoryIdAndRegisterDate(createOrUpdateBudgetRequest.getUserCategoryId(),
			createOrUpdateBudgetRequest.getRegisterDate()).ifPresentOrElse(existBudget -> {
				existBudget.validateOwner(authUser.getId());
				existBudget.changeAmount(createOrUpdateBudgetRequest.getAmount());
			}, () -> budgetRepository.save(createOrUpdateBudgetRequest.toEntity(authUser, userCategory)));
	}

	@Transactional(readOnly = true)
	public List<FindBudgetData> searchUserCategoriesWithBudget(Long userId, YearMonth registerDate) {
		return budgetQueryRepository.searchUserCategoriesWithBudget(userId, registerDate);
	}

	@Transactional(readOnly = true)
	public FindBudgetWithExpenditureResponse searchBudgetWithExpenditure(Long userId, Integer year, Integer month) {
		Map<Long, Long> expenditures = budgetQueryRepository
			.searchExpendituresExistBudget(userId, year, month);
		List<FindBudgetByRegisterDate> budgets = budgetQueryRepository
			.searchBudgetByRegisterDate(userId, year, month);
		long amountSum = 0L;
		long expenditureSum = 0L;
		for (FindBudgetByRegisterDate budget : budgets) {
			Long expenditureAmount = expenditures.getOrDefault(budget.getUserCategoryId(), AMOUNT_MIN);
			budget.setExpenditure(expenditureAmount);
			budget.setPercent(calcPercent(budget.getAmount(), expenditureAmount));
			amountSum += budget.getAmount();
			expenditureSum += expenditureAmount;
		}
		return new FindBudgetWithExpenditureResponse(
			makeRegisterDate(year, month),
			amountSum,
			expenditureSum,
			calcPercent(amountSum, expenditureSum),
			budgets
		);
	}

	private String makeRegisterDate(Integer year, Integer month) {
		StringBuffer sb = new StringBuffer();
		sb.append(year);
		if (Objects.nonNull(month)) {
			sb.append(YEAR_MONTH_SEPARATOR).append(month);
		}
		return sb.toString();
	}

	private Long calcPercent(Long amount, Long expenditure) {
		Long percent = 0L;
		if (amount <= AMOUNT_MIN) {
			throw new IllegalArgumentException(INVALID_AMOUNT_EXP_MSG);
		}
		if (expenditure > AMOUNT_MIN) {
			double doubleData = ((double)expenditure / amount) * PERCENTAGE;
			percent = (long)Math.floor(doubleData);
		}
		return percent;
	}
}
