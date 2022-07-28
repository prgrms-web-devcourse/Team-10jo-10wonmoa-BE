package com.prgrms.tenwonmoa.domain.accountbook.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.repository.IncomeRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class IncomeService {

	private final IncomeRepository incomeRepository;

	@Transactional
	public Long save(Income income) {
		return incomeRepository.save(income).getId();
	}

	public void setUserCategoryNull(Long userCategoryId) {
		incomeRepository.updateUserCategoryAsNull(userCategoryId);
	}
}
