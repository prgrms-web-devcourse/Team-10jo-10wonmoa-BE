package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.util.List;

import com.prgrms.tenwonmoa.domain.common.page.PageCustomImpl;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;

import lombok.Getter;

@Getter
public final class FindAccountBookResponse extends PageCustomImpl<AccountBookItem> {
	private FindAccountBookResponse(PageCustomRequest pageRequest, long totalElements, List<AccountBookItem> results) {
		super(pageRequest, totalElements, results);
	}

	public static FindAccountBookResponse of(PageCustomRequest pageRequest, long totalElements,
		List<AccountBookItem> results) {
		return new FindAccountBookResponse(pageRequest, totalElements, results);
	}
}
