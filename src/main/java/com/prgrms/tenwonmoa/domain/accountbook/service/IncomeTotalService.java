package com.prgrms.tenwonmoa.domain.accountbook.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.dto.CreateIncomeRequest;
import com.prgrms.tenwonmoa.domain.accountbook.dto.UpdateIncomeRequest;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.service.UserCategoryService;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class IncomeTotalService {
	private final UserService userService;
	private final UserCategoryService userCategoryService;
	private final IncomeService incomeService;

	@Transactional
	public Long createIncome(Long userId, CreateIncomeRequest createIncomeRequest) {
		User user = userService.findById(userId);
		UserCategory userCategory = userCategoryService.getById(createIncomeRequest.getUserCategoryId());
		Income income = createIncomeRequest.toEntity(user, userCategory, userCategory.getCategory().getName());

		return incomeService.save(income);
	}

	@Transactional
	public void updateIncome(Long userId, Long incomeId, UpdateIncomeRequest updateIncomeRequest) {
		Income income = incomeService.findIdAndUserId(incomeId, userId);
		UserCategory userCategory = userCategoryService.getById(updateIncomeRequest.getUserCategoryId());

		income.update(userCategory, updateIncomeRequest);
	}
}
