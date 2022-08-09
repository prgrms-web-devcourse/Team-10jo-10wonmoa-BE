package com.prgrms.tenwonmoa.domain.budget.service;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.budget.dto.FindBudgetData;
import com.prgrms.tenwonmoa.domain.budget.repository.BudgetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BudgetService {
	private final BudgetRepository budgetRepository;

	public List<FindBudgetData> findByUserIdAndRegisterDate(Long userId, YearMonth registerDate) {
		return budgetRepository.findByUserIdAndRegisterDate(userId, registerDate)
			.stream()
			.map(FindBudgetData::of)
			.collect(Collectors.toList());
	}
}
