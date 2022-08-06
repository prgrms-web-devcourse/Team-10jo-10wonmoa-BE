package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.time.LocalDate;
import java.util.List;

import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FindAccountBookResponse {

	private final List<Result> results;

	private final Long incomeSum;

	private final Long expenditureSum;

	private Integer currentPage;

	private Integer nextPage;

	public static FindAccountBookResponse of(List<Result> results, Long incomeSum, Long expenditureSum) {
		return new FindAccountBookResponse(results, incomeSum, expenditureSum);
	}

	public void setPageInfo(PageCustomRequest pageRequest) {
		this.currentPage = pageRequest.getPage();
		this.nextPage = this.currentPage + 1;

		// 마지막 페이지일 경우
		if (this.results.size() < pageRequest.getSize()) {
			nextPage = null;
		}
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
