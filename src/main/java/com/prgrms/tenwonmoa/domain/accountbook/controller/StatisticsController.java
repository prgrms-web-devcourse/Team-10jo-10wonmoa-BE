package com.prgrms.tenwonmoa.domain.accountbook.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.tenwonmoa.domain.accountbook.dto.statistics.FindStatisticsRequest;
import com.prgrms.tenwonmoa.domain.accountbook.dto.statistics.FindStatisticsResponse;
import com.prgrms.tenwonmoa.domain.accountbook.service.StatisticsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/statistics")
public class StatisticsController {
	private final StatisticsService statisticsService;

	@GetMapping
	public ResponseEntity<FindStatisticsResponse> findStatistics(
		@AuthenticationPrincipal Long userId,
		@Valid FindStatisticsRequest findStatisticsRequest
	) {
		return ResponseEntity.ok(statisticsService.searchStatistics(userId,
			findStatisticsRequest.getYear(),
			findStatisticsRequest.getMonth()));
	}
}
