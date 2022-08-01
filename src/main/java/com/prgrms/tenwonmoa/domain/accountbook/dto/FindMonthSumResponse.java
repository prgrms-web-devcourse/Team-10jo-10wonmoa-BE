package com.prgrms.tenwonmoa.domain.accountbook.dto;

import lombok.Getter;

@Getter
public class FindMonthSumResponse {

	private final Long monthIncome;

	private final Long monthExpenditure;

	private final Long monthTotal;

	public FindMonthSumResponse(Long monthIncome, Long monthExpenditure, Long monthTotal) {
		this.monthIncome = monthIncome;
		this.monthExpenditure = monthExpenditure;
		this.monthTotal = monthTotal;
	}
}
