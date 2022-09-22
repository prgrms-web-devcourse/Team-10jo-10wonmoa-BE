package com.prgrms.tenwonmoa.domain.accountbook.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prgrms.tenwonmoa.domain.accountbook.dto.statistics.FindStatisticsData;
import com.prgrms.tenwonmoa.domain.accountbook.dto.statistics.FindStatisticsResponse;
import com.prgrms.tenwonmoa.domain.accountbook.repository.StatisticsQueryRepository;

@DisplayName("통계 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {
	private static final List<String> INCOME_DEFAULT = List.of("용돈", "상여", "금융소득");
	private static final List<String> EXPENDITURE_DEFAULT = List.of("교통/차량", "문화생활", "마트/편의점");
	private static final Long AUTH_ID = 1L;
	private static final Integer YEAR = 2022;
	private static final Integer MONTH = 01;
	private static List<FindStatisticsData> incomes = List.of(
		new FindStatisticsData(INCOME_DEFAULT.get(0), 30L),
		new FindStatisticsData(INCOME_DEFAULT.get(1), 20L),
		new FindStatisticsData(INCOME_DEFAULT.get(2), 10L)
	);
	private static List<FindStatisticsData> expenditures = List.of(
		new FindStatisticsData(EXPENDITURE_DEFAULT.get(0), 45L),
		new FindStatisticsData(EXPENDITURE_DEFAULT.get(1), 44L),
		new FindStatisticsData(EXPENDITURE_DEFAULT.get(2), 43L)
	);
	@Mock
	private StatisticsQueryRepository statisticsQueryRepository;
	@InjectMocks
	private StatisticsService statisticsService;

	@Test
	void 통계_조회_성공_행위검증() {
		given(statisticsQueryRepository.searchIncomeByRegisterDate(anyLong(), anyInt(), anyInt()))
			.willReturn(incomes);
		given(statisticsQueryRepository.searchExpenditureByRegisterDate(anyLong(), anyInt(), anyInt()))
			.willReturn(expenditures);
		// when
		statisticsService.searchStatistics(AUTH_ID, YEAR, MONTH);
		// then
		verify(statisticsQueryRepository).searchIncomeByRegisterDate(anyLong(), anyInt(), anyInt());
		verify(statisticsQueryRepository).searchExpenditureByRegisterDate(anyLong(), anyInt(), anyInt());
	}

	@Test
	void 통계_조회_성공_상태검증() {
		given(statisticsQueryRepository.searchIncomeByRegisterDate(anyLong(), anyInt(), anyInt()))
			.willReturn(incomes);
		given(statisticsQueryRepository.searchExpenditureByRegisterDate(anyLong(), anyInt(), anyInt()))
			.willReturn(expenditures);
		// when
		FindStatisticsResponse findStatisticsResponse = statisticsService.searchStatistics(AUTH_ID, YEAR, MONTH);
		// then
		assertThat(findStatisticsResponse.getYear()).isEqualTo(YEAR);
		assertThat(findStatisticsResponse.getMonth()).isEqualTo(MONTH);
		assertThat(findStatisticsResponse.getIncomeTotalSum()).isEqualTo(60L);
		assertThat(findStatisticsResponse.getExpenditureTotalSum()).isEqualTo(132L);
		assertThat(findStatisticsResponse.getIncomes()).hasSize(3);
		assertThat(findStatisticsResponse.getExpenditures()).hasSize(3);
		assertThat(findStatisticsResponse.getIncomes()).extracting("percent")
			.containsExactly(50.0, 33.33, 16.67);
		assertThat(findStatisticsResponse.getExpenditures()).extracting("percent")
			.containsExactly(34.09, 33.33, 32.58);
	}

	@Test
	void 통계_조회_성공_상태검증_비어있는경우() {
		given(statisticsQueryRepository.searchIncomeByRegisterDate(anyLong(), anyInt(), anyInt()))
			.willReturn(Collections.emptyList());
		given(statisticsQueryRepository.searchExpenditureByRegisterDate(anyLong(), anyInt(), anyInt()))
			.willReturn(Collections.emptyList());

		// when
		FindStatisticsResponse findStatisticsResponse = statisticsService.searchStatistics(AUTH_ID, YEAR, MONTH);

		// then
		assertThat(findStatisticsResponse.getYear()).isEqualTo(YEAR);
		assertThat(findStatisticsResponse.getMonth()).isEqualTo(MONTH);
		assertThat(findStatisticsResponse.getIncomeTotalSum()).isEqualTo(0L);
		assertThat(findStatisticsResponse.getExpenditureTotalSum()).isEqualTo(0L);
		assertThat(findStatisticsResponse.getIncomes()).isEmpty();
		assertThat(findStatisticsResponse.getExpenditures()).isEmpty();
	}
}
