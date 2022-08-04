package com.prgrms.tenwonmoa.domain.accountbook.dto;

import lombok.Getter;

@Getter
public class FindSumResponse {

	private final Long incomeSum;

	private final Long expenditureSum;

	private final Long totalSum;

	public FindSumResponse(Long incomeSum, Long expenditureSum) {
		this.incomeSum = incomeSum;
		this.expenditureSum = expenditureSum;
		this.totalSum = incomeSum - expenditureSum;
	}
}
