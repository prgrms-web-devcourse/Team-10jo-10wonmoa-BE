package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FindAccountBookResponse {

	private final List<Result> results;

	private final Long incomeSum;

	private final Long expenditureSum;

	public static FindAccountBookResponse of(List<Result> results, Long incomeSum, Long expenditureSum) {
		return new FindAccountBookResponse(results, incomeSum, expenditureSum);
	}

	@Getter
	@RequiredArgsConstructor
	public static class Result {

		private final LocalDate registerDate;

		private final Long amount;

		private final String content;

		private final Long id;

		private final String type;

		private final String categoryName;

	}

}
