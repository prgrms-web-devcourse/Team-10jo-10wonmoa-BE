package com.prgrms.tenwonmoa.domain.accountbook.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.accountbook.dto.FindDayAccountResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindMonthSumResponse;
import com.prgrms.tenwonmoa.domain.accountbook.repository.AccountBookQueryRepository;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomImpl;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountBookQueryService {

	private final AccountBookQueryRepository accountBookQueryRepository;

	public PageCustomImpl<FindDayAccountResponse> findDailyAccount(Long userId, PageCustomRequest pageRequest,
		LocalDate date) {
		return accountBookQueryRepository.findDailyAccount(userId, pageRequest, date);
	}

	public FindMonthSumResponse findMonthSum(Long userId, LocalDate monthTime) {
		return accountBookQueryRepository.findMonthSum(userId, monthTime);
	}
}
