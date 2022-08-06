package com.prgrms.tenwonmoa.domain.accountbook.dto.statistics;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FindStatisticsData {
	private String name;
	private Long total;
	private Double percent;

	public FindStatisticsData(String name, Long total) {
		this.name = name;
		this.total = total;
	}

	public void setPercent(Double percent) {
		this.percent = percent;
	}
}
