package com.prgrms.tenwonmoa.domain.accountbook.controller;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.tenwonmoa.domain.accountbook.dto.CreateExpenditureRequest;
import com.prgrms.tenwonmoa.domain.accountbook.dto.CreateExpenditureResponse;
import com.prgrms.tenwonmoa.domain.accountbook.service.ExpenditureService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/expenditures")
public class ExpenditureController {

	private final ExpenditureService expenditureService;

	@PostMapping
	public ResponseEntity<CreateExpenditureResponse> createExpenditure(
		@RequestBody CreateExpenditureRequest createExpenditureRequest) throws URISyntaxException {
		Long userId = 1L; // 추후 Auth로 받을 예정
		CreateExpenditureResponse response = expenditureService.createExpenditure(userId, createExpenditureRequest);

		URI uri = new URI("/api/v1/expenditures/" + response.getId());
		return ResponseEntity.created(uri).body(response);
	}

}
