package com.prgrms.tenwonmoa.domain.accountbook.dto;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;

@Getter
public class DayDetail {

	private final Long id;

	private final String type;

	private final Long amount;

	private final String content;

	private final String categoryName;

	@QueryProjection
	public DayDetail(Long id, String type, Long amount, String content, String categoryName) {
		this.id = id;
		this.type = type;
		this.amount = amount;
		this.content = content;
		this.categoryName = categoryName;
	}
}
