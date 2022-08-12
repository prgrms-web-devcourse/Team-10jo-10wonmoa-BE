package com.prgrms.tenwonmoa.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.accountbook.repository.ExpenditureRepository;
import com.prgrms.tenwonmoa.domain.accountbook.repository.IncomeRepository;
import com.prgrms.tenwonmoa.domain.budget.repository.BudgetRepository;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDeleteService {

	private final IncomeRepository incomeRepository;
	private final ExpenditureRepository expenditureRepository;
	private final BudgetRepository budgetRepository;
	private final UserCategoryRepository userCategoryRepository;
	private final UserRepository userRepository;

	@Transactional
	public void deleteUserData(Long userId) {
		incomeRepository.deleteAllByUserIdInQuery(userId);
		expenditureRepository.deleteAllByUserIdInQuery(userId);
		budgetRepository.deleteAllByUserIdInQuery(userId);
		userCategoryRepository.deleteAllByUserIdInQuery(userId);
		userRepository.deleteById(userId);
	}

}
