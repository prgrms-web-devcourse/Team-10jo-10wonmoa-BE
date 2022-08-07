package com.prgrms.tenwonmoa.domain.accountbook.dto;

import lombok.Getter;

@Getter
public class MonthDetail extends FindSumResponse {

	private final int month;

	public MonthDetail(Long incomeSum, Long expenditureSum, int month) {
		super(incomeSum, expenditureSum);
		this.month = month;
	}
}
