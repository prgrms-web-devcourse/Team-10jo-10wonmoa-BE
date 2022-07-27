package com.prgrms.tenwonmoa.domain.accountbook.controller;

import java.net.URI;
import java.net.URISyntaxException;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.tenwonmoa.domain.accountbook.dto.CreateExpenditureRequest;
import com.prgrms.tenwonmoa.domain.accountbook.dto.CreateExpenditureResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindExpenditureResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.UpdateExpenditureRequest;
import com.prgrms.tenwonmoa.domain.accountbook.service.ExpenditureService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/expenditures")
public class ExpenditureController {

	private final ExpenditureService expenditureService;
	private static final String LOCATION_PREFIX = "/api/v1/expenditures/";

	@PostMapping
	public ResponseEntity<CreateExpenditureResponse> createExpenditure(
		@Valid @RequestBody CreateExpenditureRequest createExpenditureRequest) throws URISyntaxException {
		Long userId = 1L; // 추후 Auth로 받을 예정
		CreateExpenditureResponse response = expenditureService.createExpenditure(userId, createExpenditureRequest);

		URI uri = new URI(LOCATION_PREFIX + response.getId());
		return ResponseEntity.created(uri).body(response);
	}

	@PutMapping("/{expenditureId}")
	public ResponseEntity<Void> updateExpenditure(
		@PathVariable Long expenditureId,
		@Valid @RequestBody UpdateExpenditureRequest updateExpenditureRequest
	) {
		Long userId = 1L; // 추후 Auth로 받을 예정
		expenditureService.updateExpenditure(userId, expenditureId, updateExpenditureRequest);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/{expenditureId}")
	public ResponseEntity<FindExpenditureResponse> findExpenditure(@PathVariable Long expenditureId) {
		Long userId = 1L; // 추후 Auth로 받을 예정

		FindExpenditureResponse response = expenditureService.findExpenditure(userId, expenditureId);

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{expenditureId}")
	public ResponseEntity<Void> deleteExpenditure(@PathVariable Long expenditureId) {
		Long userId = 1L; // 추후 Auth로 받을 예정
		expenditureService.deleteExpenditure(expenditureId);

		return ResponseEntity.noContent().build();
	}
}
