package com.prgrms.tenwonmoa.domain.accountbook.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonFormat;
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

	@GetMapping("/daily/{date}/")
	public ResponseEntity<PageCustomImpl<FindDayAccountResponse>> findDailyAccount(
		@RequestBody PageCustomRequest pageRequest,
		@PathVariable
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
			LocalDate date
	) {
		Long userId = 1L; // 추후 Auth 받은 후 수정 필요
		PageCustomImpl<FindDayAccountResponse> response = accountBookQueryService.findDailyAccount(userId, pageRequest,
			date);
		return ResponseEntity.ok().body(response);
	}

	@GetMapping("/sum/{date}")
	public ResponseEntity<FindMonthSumResponse> findMonthSum(
		@PathVariable
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
			LocalDate date
	) {
		// 추후 Auth 받은 후 수정 필요
		Long userId = 1L;

		FindMonthSumResponse response = accountBookQueryService.findMonthSum(userId, date);
		return ResponseEntity.ok(response);
	}
}

