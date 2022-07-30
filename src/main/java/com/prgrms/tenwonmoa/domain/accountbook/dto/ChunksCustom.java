package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.util.List;

public class ChunksCustom<T> {

	protected List<T> results;

	public ChunksCustom(List<T> results) {
		this.results = results;
	}
}
