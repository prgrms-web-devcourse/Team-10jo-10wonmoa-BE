package com.prgrms.tenwonmoa.domain.accountbook.dto;

import lombok.Getter;

@Getter
public class FindDaySumResponse {

	private final Long dayIncome;

	private final Long dayExpenditure;

	private final Long dayTotal;

	public FindDaySumResponse(Long dayIncome, Long dayExpenditure, Long dayTotal) {
		this.dayIncome = dayIncome;
		this.dayExpenditure = dayExpenditure;
		this.dayTotal = dayTotal;
	}
}
