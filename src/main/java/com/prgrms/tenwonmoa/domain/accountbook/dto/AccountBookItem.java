package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class AccountBookItem implements Comparable<AccountBookItem> {

	private final Long id;

	private final String type;

	private final Long amount;

	private final String content;

	private final String categoryName;

	private final LocalDateTime registerTime;

	public AccountBookItem(Long id, String type, Long amount, String content, String categoryName,
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
	 * 1.type: EXPENDITURE, INCOME 순으로 정렬
	 * 2.type이 같으면 등록한 최신 순으로
	 * */
	@Override
	public int compareTo(AccountBookItem dayDetail) {
		if (this.type.equals(dayDetail.type)) {
			return this.registerTime.isAfter(dayDetail.registerTime) ? -1 : 1;
		}

		return this.type.compareTo(dayDetail.getType());
	}
}
