package com.prgrms.tenwonmoa.domain.common.page;

import static com.google.common.base.Preconditions.*;

import lombok.Getter;

@Getter
public class PageCustomRequest {

	private final int page;

	private int size;

	public PageCustomRequest(int page, int size) {

		checkArgument(page >= 1, "page는 1이상이어야 합니다.");

		this.page = page;
		this.size = size;

		/**
		 * TODO : default size를 추후 상수로 뺄 것을 고려
		 * */
		if (size == 0) {
			this.size = 10;
		}
	}

	public long getOffset() {
		return (long)(page - 1) * (long)size;
	}

}