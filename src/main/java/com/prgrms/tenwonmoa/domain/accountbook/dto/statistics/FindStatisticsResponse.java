package com.prgrms.tenwonmoa.domain.accountbook.dto.statistics;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FindStatisticsResponse {
	private Integer year;
	private Integer month;
	private Long incomeTotalSum;
	private Long expenditureTotalSum;
	private List<FindStatisticsData> incomes;
	private List<FindStatisticsData> expenditures;
}
