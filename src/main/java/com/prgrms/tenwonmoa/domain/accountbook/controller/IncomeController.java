package com.prgrms.tenwonmoa.domain.accountbook.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.tenwonmoa.domain.accountbook.dto.CreateIncomeRequest;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindIncomeResponse;
import com.prgrms.tenwonmoa.domain.accountbook.service.IncomeService;
import com.prgrms.tenwonmoa.domain.accountbook.service.IncomeTotalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/incomes")
public class IncomeController {

	private static final String LOCATION_PREFIX = "/api/v1/incomes/";

	private final IncomeTotalService incomeTotalService;
	private final IncomeService incomeService;

	@PostMapping
	public ResponseEntity<Long> createIncome(@RequestBody @Valid CreateIncomeRequest request) {
		Long userId = 1L; // TODO user 정보를 시큐리티 컨텍스에서 찾도록 변경한다.
		Long createdId = incomeTotalService.createIncome(userId, request);

		String redirectUri = LOCATION_PREFIX + createdId;
		return ResponseEntity.created(URI.create(redirectUri)).body(createdId);
	}

	@GetMapping("/{incomeId}")
	public FindIncomeResponse findIncome(@PathVariable Long incomeId) {
		Long userId = 1L; // TODO user 정보를 시큐리티 컨텍스에서 찾도록 변경한다.
		return incomeService.findIncome(incomeId, userId);
	}
}
