package com.prgrms.tenwonmoa.domain.accountbook.service;

import static com.prgrms.tenwonmoa.exception.message.Message.*;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.aop.annotation.ValidateIncome;
import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.dto.income.FindIncomeResponse;
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

	@ValidateIncome
	public FindIncomeResponse findIncome(Long authId, Long incomeId) {
		return FindIncomeResponse.of(findById(incomeId));
	}

	public Income findById(Long incomeId) {
		return incomeRepository.findById(incomeId)
			.orElseThrow(() -> new NoSuchElementException(INCOME_NOT_FOUND.getMessage()));
	}

	public void deleteById(Long incomeId) {
		incomeRepository.deleteById(incomeId);
	}

	public void setUserCategoryNull(Long userCategoryId) {
		incomeRepository.updateUserCategoryAsNull(userCategoryId);
	}

	@Transactional
	public void updateCategoryNameField(Long userCategoryId, String categoryName) {
		incomeRepository.updateCategoryName(userCategoryId, categoryName);
	}
}
