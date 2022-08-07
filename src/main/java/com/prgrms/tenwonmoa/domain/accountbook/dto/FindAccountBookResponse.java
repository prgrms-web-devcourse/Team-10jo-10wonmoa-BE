package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.time.LocalDate;
import java.util.List;

import com.prgrms.tenwonmoa.domain.common.page.PageCustomImpl;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public final class FindAccountBookResponse extends PageCustomImpl<FindAccountBookResponse.Result> {

	private final Long incomeSum;

	private final Long expenditureSum;

	private FindAccountBookResponse(PageCustomRequest pageRequest, List<FindAccountBookResponse.Result> results,
		Long incomeSum, Long expenditureSum) {
		super(pageRequest, results);
		this.expenditureSum = expenditureSum;
		this.incomeSum = incomeSum;
	}

	public static FindAccountBookResponse of(PageCustomRequest pageRequest,
		List<FindAccountBookResponse.Result> results, Long incomeSum, Long expenditureSum) {
		return new FindAccountBookResponse(pageRequest, results, incomeSum, expenditureSum);
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
