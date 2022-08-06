package com.prgrms.tenwonmoa.domain.accountbook.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.accountbook.dto.statistics.FindStatisticsData;
import com.prgrms.tenwonmoa.domain.accountbook.dto.statistics.FindStatisticsResponse;
import com.prgrms.tenwonmoa.domain.accountbook.repository.StatisticsQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StatisticsService {
	private static final int PERCENTAGE = 100;
	private static final double DECIMAL_PLACES_BEFORE = 100;
	private static final double DECIMAL_PLACES_AFTER = 100.0;
	private final StatisticsQueryRepository statisticsQueryRepository;

	@Transactional(readOnly = true)
	public FindStatisticsResponse searchStatistics(Long authId, Integer year, Integer month) {
		List<FindStatisticsData> incomes = statisticsQueryRepository.searchIncomeByRegisterDate(authId,
			year, month);
		List<FindStatisticsData> expenditures = statisticsQueryRepository.searchExpenditureByRegisterDate(authId,
			year, month);

		Long incomeTotalSum = calcTotalSum(incomes);
		Long expenditureTotalSum = calcTotalSum(expenditures);

		setPercent(incomes, incomeTotalSum);
		setPercent(expenditures, expenditureTotalSum);
		return new FindStatisticsResponse(year, month, incomeTotalSum, expenditureTotalSum, incomes, expenditures);
	}

	private void setPercent(List<FindStatisticsData> findStatisticsDataList, Long totalSum) {
		findStatisticsDataList.forEach(findStatisticsData -> {
			Double percent = calcPercent(totalSum, findStatisticsData.getTotal());
			findStatisticsData.setPercent(percent);
		});
	}

	private Long calcTotalSum(List<FindStatisticsData> findStatisticsDataList) {
		return findStatisticsDataList.stream().mapToLong(FindStatisticsData::getTotal).sum();
	}

	private Double calcPercent(Long totalSum, Long perSum) {
		Double percent = (double)perSum / totalSum * PERCENTAGE;
		return Math.round(percent * DECIMAL_PLACES_BEFORE) / DECIMAL_PLACES_AFTER;
	}
}
