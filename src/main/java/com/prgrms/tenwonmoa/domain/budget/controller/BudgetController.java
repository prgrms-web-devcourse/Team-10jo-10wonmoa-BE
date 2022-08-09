package com.prgrms.tenwonmoa.domain.budget.controller;

import java.time.YearMonth;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.tenwonmoa.domain.budget.dto.CreateOrUpdateBudgetRequest;
import com.prgrms.tenwonmoa.domain.budget.dto.FindBudgetResponse;
import com.prgrms.tenwonmoa.domain.budget.service.BudgetService;
import com.prgrms.tenwonmoa.domain.budget.service.BudgetTotalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/budgets")
public class BudgetController {
	private static final String LOCATION_PREFIX = "/api/v1/budgets";
	private final BudgetTotalService budgetTotalService;
	private final BudgetService budgetService;

	@PutMapping
	public ResponseEntity<Void> createOrUpdate(@RequestBody @Valid CreateOrUpdateBudgetRequest request,
		@AuthenticationPrincipal Long userId) {
		budgetTotalService.createOrUpdateBudget(userId, request);

		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<FindBudgetResponse> findBudgets(@AuthenticationPrincipal Long userId,
		@RequestParam YearMonth registerDate) {
		FindBudgetResponse findBudgetResponse = new FindBudgetResponse(
			budgetService.findByUserIdAndRegisterDate(userId, registerDate)
		);
		return ResponseEntity.ok(findBudgetResponse);
	}
}
