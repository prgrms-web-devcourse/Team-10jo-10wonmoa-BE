package com.prgrms.tenwonmoa.domain.accountbook.controller;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
		@RequestParam @NotBlank @Min(1900) Integer year,
		@RequestParam(required = false) @Min(1) @Max(12) Integer month
	) {
		return ResponseEntity.ok(statisticsService.searchStatistics(userId,
			year,
			month));
	}
}
