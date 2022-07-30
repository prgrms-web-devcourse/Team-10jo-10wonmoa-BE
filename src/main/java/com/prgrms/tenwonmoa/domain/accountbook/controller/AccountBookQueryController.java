package com.prgrms.tenwonmoa.domain.accountbook.controller;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.tenwonmoa.domain.accountbook.dto.FindAccountDayResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.PageCustomImpl;
import com.prgrms.tenwonmoa.domain.accountbook.dto.PageCustomRequest;
import com.prgrms.tenwonmoa.domain.accountbook.service.AccountBookQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account-book")
public class AccountBookQueryController {

	private final AccountBookQueryService accountBookQueryService;

	@GetMapping("/daily/{month}/")
	public ResponseEntity<PageCustomImpl<FindAccountDayResponse>> findDailyAccount(
		@RequestBody PageCustomRequest pageRequest,
		@PathVariable("month") @Min(1) @Max(12) int month
	) {
		PageCustomImpl<FindAccountDayResponse> response = accountBookQueryService.findDailyAccount(pageRequest, month);
		return ResponseEntity.ok().body(response);
	}

}
