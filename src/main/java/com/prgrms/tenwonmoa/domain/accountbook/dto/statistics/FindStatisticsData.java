package com.prgrms.tenwonmoa.domain.accountbook.dto.statistics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class FindStatisticsData {
	private String name;
	private Long total;
}
