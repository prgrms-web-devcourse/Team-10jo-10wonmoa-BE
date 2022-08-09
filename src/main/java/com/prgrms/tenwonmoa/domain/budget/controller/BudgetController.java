package com.prgrms.tenwonmoa.domain.budget.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.tenwonmoa.domain.budget.dto.CreateOrUpdateBudgetRequest;
import com.prgrms.tenwonmoa.domain.budget.service.BudgetTotalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/budgets")
public class BudgetController {
	private static final String LOCATION_PREFIX = "/api/v1/budgets";

	private final BudgetTotalService budgetTotalService;

	@PutMapping
	public ResponseEntity<Void> createIncome(@RequestBody @Valid CreateOrUpdateBudgetRequest request,
		@AuthenticationPrincipal Long userId) {
		budgetTotalService.createOrUpdateBudget(userId, request);

		return ResponseEntity.noContent().build();
	}
}
