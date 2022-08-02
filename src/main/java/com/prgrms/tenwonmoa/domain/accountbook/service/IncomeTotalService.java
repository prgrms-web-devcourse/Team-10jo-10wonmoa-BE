package com.prgrms.tenwonmoa.domain.accountbook.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.dto.income.CreateIncomeRequest;
import com.prgrms.tenwonmoa.domain.accountbook.dto.income.UpdateIncomeRequest;
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

	public Long createIncome(Long userId, CreateIncomeRequest createIncomeRequest) {
		UserCategory userCategory = userCategoryService.findById(createIncomeRequest.getUserCategoryId());

		User authUser = userService.findById(userId);
		Income income = createIncomeRequest.toEntity(authUser, userCategory, userCategory.getCategory().getName());
		return incomeService.save(income);
	}

	public void updateIncome(Long authId, Long incomeId, UpdateIncomeRequest updateIncomeRequest) {
		Income income = incomeService.findById(incomeId);
		income.getUser().validateLogin(authId);
		UserCategory userCategory = userCategoryService.findById(updateIncomeRequest.getUserCategoryId());
		income.update(userCategory, updateIncomeRequest);
	}

	public void deleteIncome(Long incomeId, Long authId) {
		Income income = incomeService.findById(incomeId);
		income.getUser().validateLogin(authId);
		incomeService.deleteById(incomeId);
	}

}
