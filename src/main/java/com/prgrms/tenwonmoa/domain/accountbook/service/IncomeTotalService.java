package com.prgrms.tenwonmoa.domain.accountbook.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.dto.income.CreateIncomeRequest;
import com.prgrms.tenwonmoa.domain.accountbook.dto.income.UpdateIncomeRequest;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.service.UserCategoryService;
import com.prgrms.tenwonmoa.domain.user.User;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class IncomeTotalService {
	private final UserCategoryService userCategoryService;
	private final IncomeService incomeService;

	public Long createIncome(User authUser, CreateIncomeRequest createIncomeRequest) {
		UserCategory userCategory = userCategoryService.findById(createIncomeRequest.getUserCategoryId());

		Income income = createIncomeRequest.toEntity(authUser, userCategory, userCategory.getCategory().getName());
		return incomeService.save(income);
	}

	public void updateIncome(User authUser, Long incomeId, UpdateIncomeRequest updateIncomeRequest) {
		Income income = incomeService.findById(incomeId);
		authUser.validateLogin(income.getUser());
		UserCategory userCategory = userCategoryService.findById(updateIncomeRequest.getUserCategoryId());

		income.update(userCategory, updateIncomeRequest);
	}

	public void deleteIncome(Long incomeId, User authUser) {
		Income income = incomeService.findById(incomeId);
		authUser.validateLogin(income.getUser());

		incomeService.deleteById(incomeId);
	}
}
