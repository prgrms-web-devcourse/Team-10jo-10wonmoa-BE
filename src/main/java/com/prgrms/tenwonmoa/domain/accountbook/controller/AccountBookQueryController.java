package com.prgrms.tenwonmoa.domain.accountbook.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.tenwonmoa.domain.accountbook.dto.FindDayAccountResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindMonthSumResponse;
import com.prgrms.tenwonmoa.domain.accountbook.service.AccountBookQueryService;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomImpl;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account-book")
public class AccountBookQueryController {

	private final AccountBookQueryService accountBookQueryService;

	@GetMapping("/daily/{date}")
	public ResponseEntity<PageCustomImpl<FindDayAccountResponse>> findDailyAccount(
		@AuthenticationPrincipal Long userId,
		@RequestBody PageCustomRequest pageRequest,
		@PathVariable
		@DateTimeFormat(pattern = "yyyy-MM-dd")
			LocalDate date
	) {
		PageCustomImpl<FindDayAccountResponse> response = accountBookQueryService.findDailyAccount(userId, pageRequest,
			date);
		return ResponseEntity.ok().body(response);
	}

	@GetMapping("/sum/{date}")
	public ResponseEntity<FindMonthSumResponse> findMonthSum(
		@AuthenticationPrincipal Long userId,
		@PathVariable
		@DateTimeFormat(pattern = "yyyy-MM-dd")
			LocalDate date
	) {
		FindMonthSumResponse response = accountBookQueryService.findMonthSum(userId, date);
		return ResponseEntity.ok(response);
	}
}

