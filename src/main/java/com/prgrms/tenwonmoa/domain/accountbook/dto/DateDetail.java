package com.prgrms.tenwonmoa.domain.accountbook.dto;

import lombok.Getter;

@Getter
public class DateDetail extends FindSumResponse {

	private final int day;

	public DateDetail(int day, Long incomeSum, Long expenditureSum) {
		super(incomeSum, expenditureSum);
		this.day = day;
	}
}
