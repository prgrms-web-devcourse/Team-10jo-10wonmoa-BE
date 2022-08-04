package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class FindMonthAccountResponse {

	private final List<MonthDetail> results;

	public FindMonthAccountResponse(List<MonthDetail> results) {
		this.results = results;
	}
}
