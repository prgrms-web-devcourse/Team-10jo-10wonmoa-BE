package com.prgrms.tenwonmoa.domain.accountbook.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.accountbook.dto.FindCalendarResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindDayAccountResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindMonthAccountResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindSumResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.MonthCondition;
import com.prgrms.tenwonmoa.domain.accountbook.dto.YearMonthCondition;
import com.prgrms.tenwonmoa.domain.accountbook.repository.AccountBookQueryRepository;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;
import com.prgrms.tenwonmoa.domain.common.page.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountBookQueryService {

	private final AccountBookQueryRepository accountBookQueryRepository;

	public PageResponse<FindDayAccountResponse> findDailyAccount(Long userId, PageCustomRequest pageRequest,
		LocalDate date) {
		return accountBookQueryRepository.findDailyAccount(userId, pageRequest, date);
	}

	public FindSumResponse findMonthSum(Long userId, LocalDate monthTime) {
		return accountBookQueryRepository.findMonthSum(userId, monthTime);
	}

	public FindSumResponse findYearSum(Long userId, int year) {
		return accountBookQueryRepository.findYearSum(userId, year);
	}

	public FindMonthAccountResponse findMonthAccount(Long userId, MonthCondition condition) {
		return accountBookQueryRepository.findMonthAccount(userId, condition);
	}

	public PageResponse<FindDayAccountResponse> findDailyAccountVer2(Long userId,
		PageCustomRequest pageRequest,
		YearMonthCondition condition) {
		return accountBookQueryRepository.findDailyAccountVer2(userId, pageRequest, condition);
	}

	public FindCalendarResponse findCalendarAccount(Long userId, YearMonthCondition condition) {
		return accountBookQueryRepository.findCalendarAccount(userId, condition);
	}
}
