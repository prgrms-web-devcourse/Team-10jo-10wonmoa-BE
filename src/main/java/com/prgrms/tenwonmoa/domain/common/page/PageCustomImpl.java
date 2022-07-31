package com.prgrms.tenwonmoa.domain.common.page;

import java.util.List;

import lombok.Getter;

@Getter
public class PageCustomImpl<T> extends ChunksCustom<T> {

	private final int currentPage;

	private Integer nextPage;

	public PageCustomImpl(int currentPage, PageCustomRequest pageRequest, List<T> results) {
		super(results);
		this.currentPage = pageRequest.getPage();
		this.nextPage = this.currentPage + 1;

		// 마지막 페이지일 경우
		if (this.results.size() < pageRequest.getSize()) {
			nextPage = null;
		}
	}
}
