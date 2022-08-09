package com.prgrms.tenwonmoa.domain.accountbook.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.dto.income.CreateIncomeRequest;
import com.prgrms.tenwonmoa.domain.accountbook.dto.income.UpdateIncomeRequest;
import com.prgrms.tenwonmoa.domain.accountbook.repository.ExpenditureRepository;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.service.UserCategoryService;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class IncomeTotalService {
	private final UserCategoryService userCategoryService;
	private final IncomeService incomeService;
	private final UserService userService;
	private final ExpenditureRepository expenditureRepository;

	public Long createIncome(Long userId, CreateIncomeRequest createIncomeRequest) {
		UserCategory userCategory = userCategoryService.findById(createIncomeRequest.getUserCategoryId());
		userCategory.getUser().validateLoginUser(userId);
		User authUser = userService.findById(userId);
		Income income = createIncomeRequest.toEntity(authUser, userCategory, userCategory.getCategory().getName());
		return incomeService.save(income);
	}

	public void updateIncome(Long authId, Long incomeId, UpdateIncomeRequest updateIncomeRequest) {
		Income income = incomeService.findById(incomeId);
		income.validateOwner(authId);
		UserCategory userCategory = userCategoryService.findById(updateIncomeRequest.getUserCategoryId());
		if (CategoryType.isExpenditure(userCategory.getCategory().getCategoryType())) {
			incomeService.deleteById(incomeId);
			expenditureRepository.save(
				updateIncomeRequest.toExpenditure(income.getUser(), userCategory, userCategory.getCategoryName()));
		} else {
			income.update(userCategory, updateIncomeRequest);
		}
	}

	public void deleteIncome(Long incomeId, Long authId) {
		Income income = incomeService.findById(incomeId);
		income.validateOwner(authId);
		incomeService.deleteById(incomeId);
	}

}
