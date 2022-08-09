package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.util.List;

import com.prgrms.tenwonmoa.domain.common.page.PageCustomImpl;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;

import lombok.Getter;

@Getter
public final class FindAccountBookResponse<T> extends PageCustomImpl<T> {

	private final Long incomeSum;

	private final Long expenditureSum;

	private final Long totalSum;

	private FindAccountBookResponse(PageCustomRequest pageRequest, List<T> results,
		Long incomeSum, Long expenditureSum) {
		super(pageRequest, results);
		this.expenditureSum = expenditureSum;
		this.incomeSum = incomeSum;
		this.totalSum = incomeSum - expenditureSum;
	}

	public static <T> FindAccountBookResponse<T> of(PageCustomRequest pageRequest,
		List<T> results, Long incomeSum, Long expenditureSum) {
		return new FindAccountBookResponse<>(pageRequest, results, incomeSum, expenditureSum);
	}
}
