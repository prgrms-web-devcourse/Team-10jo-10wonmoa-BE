package com.prgrms.tenwonmoa.domain.budget.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		userCategory.getUser().validateLoginUser(userId);
		User authUser = userService.findById(userId);

		budgetRepository.findByUserCategoryIdAndRegisterDate(createOrUpdateBudgetRequest.getUserCategoryId(),
			createOrUpdateBudgetRequest.getRegisterDate()).ifPresentOrElse(existBudget -> {
				existBudget.validateOwner(authUser.getId());
				existBudget.changeAmount(createOrUpdateBudgetRequest.getAmount());
			}, () -> budgetRepository.save(createOrUpdateBudgetRequest.toEntity(authUser, userCategory)));
	}
}
