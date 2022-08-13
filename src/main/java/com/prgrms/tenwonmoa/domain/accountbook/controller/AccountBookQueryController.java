package com.prgrms.tenwonmoa.domain.accountbook.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.tenwonmoa.domain.accountbook.dto.FindCalendarResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindDayAccountResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindMonthAccountResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindSumResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.MonthCondition;
import com.prgrms.tenwonmoa.domain.accountbook.dto.YearMonthCondition;
import com.prgrms.tenwonmoa.domain.accountbook.service.AccountBookQueryService;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;
import com.prgrms.tenwonmoa.domain.common.page.PageResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account-book")
public class AccountBookQueryController {

	private final AccountBookQueryService accountBookQueryService;

	@GetMapping("/daily/{date}")
	public ResponseEntity<PageResponse<FindDayAccountResponse>> findDailyAccount(
		@AuthenticationPrincipal Long userId,
		@RequestParam(value = "page", defaultValue = "1") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@PathVariable
		@DateTimeFormat(pattern = "yyyy-MM-dd")
			LocalDate date
	) {
		PageCustomRequest pageRequest = new PageCustomRequest(page, size);
		PageResponse<FindDayAccountResponse> response = accountBookQueryService.findDailyAccount(userId, pageRequest,
			date);
		return ResponseEntity.ok().body(response);
	}

	@GetMapping("/daily")
	public ResponseEntity<PageResponse<FindDayAccountResponse>> findDailyAccountVer2(
		@AuthenticationPrincipal Long userId,
		@RequestParam int year,
		@RequestParam int month,
		@RequestParam(value = "page", defaultValue = "1") int page,
		@RequestParam(value = "size", defaultValue = "10") int size
	) {
		YearMonthCondition condition = new YearMonthCondition(year, month);
		PageCustomRequest pageRequest = new PageCustomRequest(page, size);
		PageResponse<FindDayAccountResponse> response = accountBookQueryService.findDailyAccountVer2(userId,
			pageRequest,
			condition);
		return ResponseEntity.ok().body(response);
	}

	@GetMapping("/sum/month/{date}")
	public ResponseEntity<FindSumResponse> findMonthSum(
		@AuthenticationPrincipal Long userId,
		@PathVariable
		@DateTimeFormat(pattern = "yyyy-MM-dd")
			LocalDate date
	) {
		FindSumResponse response = accountBookQueryService.findMonthSum(userId, date);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/sum/year/{year}")
	public ResponseEntity<FindSumResponse> findYearSum(
		@AuthenticationPrincipal Long userId,
		@PathVariable int year
	) {
		FindSumResponse response = accountBookQueryService.findYearSum(userId, year);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/month/{year}")
	public ResponseEntity<FindMonthAccountResponse> findMonthAccount(
		@AuthenticationPrincipal Long userId,
		@PathVariable int year
	) {
		MonthCondition condition = new MonthCondition(LocalDateTime.now(), year);
		FindMonthAccountResponse response = accountBookQueryService.findMonthAccount(userId, condition);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/calendar")
	public ResponseEntity<FindCalendarResponse> findCalendarAccount(
		@AuthenticationPrincipal Long userId,
		@RequestParam(value = "year") int year,
		@RequestParam("month") int month
	) {
		YearMonthCondition condition = new YearMonthCondition(year, month);
		FindCalendarResponse response = accountBookQueryService.findCalendarAccount(userId, condition);
		return ResponseEntity.ok(response);
	}

}

