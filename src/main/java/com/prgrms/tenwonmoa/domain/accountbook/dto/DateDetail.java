package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class DateDetail extends FindSumResponse {

	private final LocalDate date;

	public DateDetail(LocalDate date, Long incomeSum, Long expenditureSum) {
		super(incomeSum, expenditureSum);
		this.date = date;
	}
}
