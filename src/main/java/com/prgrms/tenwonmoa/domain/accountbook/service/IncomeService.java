package com.prgrms.tenwonmoa.domain.accountbook.service;

import static com.prgrms.tenwonmoa.exception.message.Message.*;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindIncomeResponse;
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

	public FindIncomeResponse findIncome(Long incomeId, Long userId) {
		Income findIncome = incomeRepository.findByIdAndUserId(incomeId, userId)
			.orElseThrow(() -> new NoSuchElementException(INCOME_NOT_FOUND.getMessage()));
		return FindIncomeResponse.of(findIncome);
	}
}
