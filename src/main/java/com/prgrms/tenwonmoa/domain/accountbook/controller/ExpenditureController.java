package com.prgrms.tenwonmoa.domain.accountbook.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
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

import com.prgrms.tenwonmoa.domain.accountbook.dto.expenditure.CreateExpenditureRequest;
import com.prgrms.tenwonmoa.domain.accountbook.dto.expenditure.CreateExpenditureResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.expenditure.FindExpenditureResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.expenditure.UpdateExpenditureRequest;
import com.prgrms.tenwonmoa.domain.accountbook.service.ExpenditureService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/expenditures")
public class ExpenditureController {

	private final ExpenditureService expenditureService;

	@PostMapping
	public ResponseEntity<CreateExpenditureResponse> createExpenditure(
		@Valid @RequestBody CreateExpenditureRequest createExpenditureRequest,
		@AuthenticationPrincipal Long userId) {
		CreateExpenditureResponse response = expenditureService.createExpenditure(userId, createExpenditureRequest);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{expenditureId}")
	public ResponseEntity<Void> updateExpenditure(
		@PathVariable Long expenditureId,
		@Valid @RequestBody UpdateExpenditureRequest updateExpenditureRequest,
		@AuthenticationPrincipal Long userId
	) {
		expenditureService.updateExpenditure(userId, expenditureId, updateExpenditureRequest);

		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{expenditureId}")
	public ResponseEntity<FindExpenditureResponse> findExpenditure(
		@PathVariable Long expenditureId,
		@AuthenticationPrincipal Long userId) {
		FindExpenditureResponse response = expenditureService.findExpenditure(userId, expenditureId);

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{expenditureId}")
	public ResponseEntity<Void> deleteExpenditure(
		@PathVariable Long expenditureId,
		@AuthenticationPrincipal Long userId) {
		expenditureService.deleteExpenditure(userId, expenditureId);

		return ResponseEntity.noContent().build();
	}
}
