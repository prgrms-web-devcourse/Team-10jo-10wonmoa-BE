package com.prgrms.tenwonmoa.domain.budget.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.budget.Budget;
import com.prgrms.tenwonmoa.domain.budget.dto.CreateOrUpdateBudgetRequest;
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

	public void createOrUpdateBudget(Long userId, CreateOrUpdateBudgetRequest createOrUpdateBudgetRequest) {
		UserCategory userCategory = userCategoryService.findById(createOrUpdateBudgetRequest.getUserCategoryId());
		User authUser = userService.findById(userId);

		Optional<Budget> budget = budgetRepository.findByUserCategoryIdAndRegisterDate(
			createOrUpdateBudgetRequest.getUserCategoryId(), createOrUpdateBudgetRequest.getRegisterDate());

		if (budget.isEmpty()) {
			budgetRepository.save(createOrUpdateBudgetRequest.toEntity(authUser, userCategory));
		} else {
			Budget existBudget = budget.get();
			existBudget.changeAmount(createOrUpdateBudgetRequest.getAmount());
		}
	}
}
