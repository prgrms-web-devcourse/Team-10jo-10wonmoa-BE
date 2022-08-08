package com.prgrms.tenwonmoa.domain.budget.controller;

import java.net.URI;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.tenwonmoa.domain.budget.dto.CreateBudgetRequest;
import com.prgrms.tenwonmoa.domain.budget.service.BudgetTotalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/budgets")
public class BudgetController {
	private static final String LOCATION_PREFIX = "/api/v1/budgets";

	private final BudgetTotalService budgetTotalService;

	@PostMapping
	public ResponseEntity<Map<String, Long>> createIncome(@RequestBody @Valid CreateBudgetRequest request,
		@AuthenticationPrincipal Long userId) {
		Long createdId = budgetTotalService.createBudget(userId, request);

		String redirectUri = LOCATION_PREFIX + "/" + createdId;
		return ResponseEntity.created(URI.create(redirectUri)).body(Map.of("id", createdId));
	}
}
