package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class PageCustomImpl<T> extends ChunksCustom<T> {

	private final int currentPage;

	private final int nextPage;

	public PageCustomImpl(int currentPage, int nextPage, List<T> results) {
		super(results);
		this.currentPage = currentPage;
		this.nextPage = nextPage;
	}

}
