package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class MonthCondition {

	private final LocalDateTime now;

	private final int year;

	private final boolean isFuture;

	public MonthCondition(LocalDateTime now, int year) {
		this.now = now;
		this.year = year;
		isFuture = now.getYear() < year;
	}

}
