package com.prgrms.tenwonmoa.domain.common.page;

import java.util.List;

public class PageCustomImpl<T> extends ChunksCustom<T> implements PageResponse<T> {

	private final int currentPage;

	private final int pageSize;

	private final long totalElements;

	public PageCustomImpl(PageCustomRequest pageRequest, long totalElements, List<T> results) {
		super(results);
		this.currentPage = pageRequest.getPage();
		this.pageSize = pageRequest.getSize();
		this.totalElements = totalElements;
	}

	@Override
	public int getCurrentPage() {
		return currentPage;
	}

	@Override
	public Integer getNextPage() {
		return this.currentPage == getTotalPages() ? null : currentPage + 1;
	}

	@Override
	public long getTotalElements() {
		return this.totalElements;
	}

	@Override
	public int getTotalPages() {
		if (totalElements == 0) {
			return 1;
		}
		return this.pageSize == 0 ? 1 : (int)Math.ceil((double)totalElements / (double)this.pageSize);
	}
}
