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
	@Mock
	private StatisticsQueryRepository statisticsQueryRepository;
	@InjectMocks
	private StatisticsService statisticsService;

	@Test
	void 통계_조회_성공_행위검증() {
		// given
		List<FindStatisticsData> incomes = mock(List.class);
		List<FindStatisticsData> expenditures = mock(List.class);

		given(statisticsQueryRepository.searchIncomeByRegisterDate(anyLong(), anyInt(), anyInt())).willReturn(incomes);
		given(statisticsQueryRepository.searchExpenditureByRegisterDate(anyLong(), anyInt(), anyInt())).willReturn(
			expenditures);

		// when
		statisticsService.searchStatistics(anyLong(), anyInt(), anyInt());

		// then
		verify(statisticsQueryRepository).searchIncomeByRegisterDate(anyLong(), anyInt(), anyInt());
		verify(statisticsQueryRepository).searchExpenditureByRegisterDate(anyLong(), anyInt(), anyInt());
	}

	@Test
	void 통계_조회_성공_상태검증() {
		List<FindStatisticsData> incomes = List.of(
			new FindStatisticsData(INCOME_DEFAULT.get(0), 30L),
			new FindStatisticsData(INCOME_DEFAULT.get(1), 20L),
			new FindStatisticsData(INCOME_DEFAULT.get(2), 10L));
		// given
		List<FindStatisticsData> expenditures = List.of(
			new FindStatisticsData(EXPENDITURE_DEFAULT.get(0), 45L),
			new FindStatisticsData(EXPENDITURE_DEFAULT.get(1), 44L),
			new FindStatisticsData(EXPENDITURE_DEFAULT.get(2), 43L)
		);
		given(statisticsQueryRepository.searchIncomeByRegisterDate(anyLong(), anyInt(), anyInt())).willReturn(incomes);
		given(statisticsQueryRepository.searchExpenditureByRegisterDate(anyLong(), anyInt(), anyInt())).willReturn(
			expenditures);

		// when
		FindStatisticsResponse findStatisticsResponse = statisticsService.searchStatistics(1L, 2022, 07);
		System.out.println(findStatisticsResponse);

		// then
		assertThat(findStatisticsResponse.getYear()).isEqualTo(2022);
		assertThat(findStatisticsResponse.getMonth()).isEqualTo(07);
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
		List<FindStatisticsData> incomes = Collections.emptyList();
		List<FindStatisticsData> expenditures = Collections.emptyList();

		given(statisticsQueryRepository.searchIncomeByRegisterDate(anyLong(), anyInt(), anyInt())).willReturn(incomes);
		given(statisticsQueryRepository.searchExpenditureByRegisterDate(anyLong(), anyInt(), anyInt())).willReturn(
			expenditures);

		// when
		FindStatisticsResponse findStatisticsResponse = statisticsService.searchStatistics(1L, 2022, 07);

		// then
		assertThat(findStatisticsResponse.getYear()).isEqualTo(2022);
		assertThat(findStatisticsResponse.getMonth()).isEqualTo(07);
		assertThat(findStatisticsResponse.getIncomeTotalSum()).isEqualTo(0L);
		assertThat(findStatisticsResponse.getExpenditureTotalSum()).isEqualTo(0L);
		assertThat(findStatisticsResponse.getIncomes()).isEmpty();
		assertThat(findStatisticsResponse.getExpenditures()).isEmpty();
	}
}
