package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class DayDetail implements Comparable<DayDetail> {

	private final Long id;

	private final String type;

	private final Long amount;

	private final String content;

	private final String categoryName;

	private final LocalDateTime registerTime;

	public DayDetail(Long id, String type, Long amount, String content, String categoryName,
		LocalDateTime registerTime) {
		this.id = id;
		this.type = type;
		this.amount = amount;
		this.content = content;
		this.categoryName = categoryName;
		this.registerTime = registerTime;
	}

	/**
	 * 날짜별 상세 정렬 기준
	 * 1.type: INCOME, EXPENDITURE 순으로 정렬
	 * 2.type이 같으면 등록한 최신 순으로
	 * */
	@Override
	public int compareTo(DayDetail d) {
		if (this.type.equals(d.type))
			return this.registerTime.isAfter(d.registerTime) ? -1 : 1;

		return this.type.compareTo(d.getType());
	}
}
