package com.prgrms.tenwonmoa.domain.budget.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.budget.dto.CreateBudgetRequest;
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
	private final BudgetRepository budgetRepository;
	private final UserService userService;
	private final UserCategoryService userCategoryService;

	public Long createBudget(Long userId, CreateBudgetRequest createBudgetRequest) {
		UserCategory userCategory = userCategoryService.findById(createBudgetRequest.getUserCategoryId());
		User authUser = userService.findById(userId);

		return budgetRepository.save(createBudgetRequest.toEntity(authUser, userCategory)).getId();
	}
}
