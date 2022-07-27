package com.prgrms.tenwonmoa.domain.accountbook.controller;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.tenwonmoa.domain.accountbook.dto.CreateExpenditureRequest;
import com.prgrms.tenwonmoa.domain.accountbook.dto.CreateExpenditureResponse;
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
		@RequestBody CreateExpenditureRequest createExpenditureRequest) throws URISyntaxException {
		Long userId = 1L; // 추후 Auth로 받을 예정
		CreateExpenditureResponse response = expenditureService.createExpenditure(userId, createExpenditureRequest);

		URI uri = new URI(LOCATION_PREFIX + response.getId());
		return ResponseEntity.created(uri).body(response);
	}

	@PutMapping("/{expenditureId}")
	public ResponseEntity<Void> updateExpenditure(
		@PathVariable Long expenditureId,
		@RequestBody UpdateExpenditureRequest updateExpenditureRequest) throws URISyntaxException {
		Long userId = 1L; // 추후 Auth로 받을 예정
		expenditureService.updateExpenditure(userId, expenditureId, updateExpenditureRequest);

		/**
		 * TODO : 이부분 uri 어디로 지정해줘야할 지 프로트랑 의논 필요
		 * */
		URI uri = new URI(LOCATION_PREFIX + expenditureId);
		return ResponseEntity.created(uri).build();
	}
}
