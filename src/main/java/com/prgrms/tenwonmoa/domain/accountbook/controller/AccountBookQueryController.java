package com.prgrms.tenwonmoa.domain.accountbook.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonFormat;
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
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM", timezone = "Asia/Seoul") LocalDateTime month
	) {
		Long userId = 1L; // 추후 Auth 받은 후 수정 필요
		PageCustomImpl<FindAccountDayResponse> response = accountBookQueryService.findDailyAccount(userId, pageRequest,
			month);
		return ResponseEntity.ok().body(response);
	}

}
