package com.prgrms.tenwonmoa.domain.accountbook.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.tenwonmoa.domain.accountbook.dto.CreateIncomeRequest;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindIncomeResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.UpdateIncomeRequest;
import com.prgrms.tenwonmoa.domain.accountbook.service.IncomeService;
import com.prgrms.tenwonmoa.domain.accountbook.service.IncomeTotalService;
import com.prgrms.tenwonmoa.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/incomes")
public class IncomeController {

	private static final String LOCATION_PREFIX = "/api/v1/incomes/";

	private final IncomeTotalService incomeTotalService;
	private final IncomeService incomeService;
	private final UserService userService;

	@PostMapping
	public ResponseEntity<Long> createIncome(@RequestBody @Valid CreateIncomeRequest request,
		@AuthenticationPrincipal Long userId) {
		Long createdId = incomeTotalService.createIncome(userService.findById(userId), request);

		String redirectUri = LOCATION_PREFIX + createdId;
		return ResponseEntity.created(URI.create(redirectUri)).body(createdId);
	}

	@GetMapping("/{incomeId}")
	public FindIncomeResponse findIncome(@PathVariable Long incomeId,
		@AuthenticationPrincipal Long userId) {
		return incomeService.findIncome(incomeId, userService.findById(userId));
	}

	@PutMapping("/{incomeId}")
	public ResponseEntity<Void> updateIncome(@PathVariable Long incomeId,
		@RequestBody @Valid UpdateIncomeRequest updateIncomeRequest,
		@AuthenticationPrincipal Long userId
	) {
		incomeTotalService.updateIncome(userService.findById(userId), incomeId, updateIncomeRequest);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{incomeId}")
	public ResponseEntity<Void> deleteIncome(@PathVariable Long incomeId,
		@AuthenticationPrincipal Long userId) {
		incomeTotalService.deleteIncome(incomeId, userService.findById(userId));
		return ResponseEntity.noContent().build();
	}
}
