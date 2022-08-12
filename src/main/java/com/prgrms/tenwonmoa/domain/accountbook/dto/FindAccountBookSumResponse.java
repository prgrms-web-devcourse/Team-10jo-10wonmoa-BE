package com.prgrms.tenwonmoa.domain.accountbook.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FindAccountBookSumResponse {

	private final Long incomeSum;

	private final Long expenditureSum;

	private final Long totalSum;

	public static FindAccountBookSumResponse of(Long incomeSum, Long expenditureSum) {
		incomeSum = incomeSum == null ? 0 : incomeSum;
		expenditureSum = expenditureSum == null ? 0 : expenditureSum;
		return new FindAccountBookSumResponse(incomeSum, expenditureSum, incomeSum - expenditureSum);
	}
}
