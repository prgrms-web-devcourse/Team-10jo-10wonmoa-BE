package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.util.List;

import com.prgrms.tenwonmoa.domain.common.page.ChunksCustom;

import lombok.Getter;

@Getter
public class FindCalendarResponse extends ChunksCustom<DateDetail> {

	private final int month;

	public FindCalendarResponse(int month, List<DateDetail> results) {
		super(results);
		this.month = month;
	}
}
