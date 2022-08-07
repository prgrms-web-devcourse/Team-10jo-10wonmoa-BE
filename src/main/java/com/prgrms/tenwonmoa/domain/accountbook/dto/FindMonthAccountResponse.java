package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.util.List;

import com.prgrms.tenwonmoa.domain.common.page.ChunksCustom;

import lombok.Getter;

@Getter
public class FindMonthAccountResponse extends ChunksCustom<MonthDetail> {
	public FindMonthAccountResponse(List<MonthDetail> results) {
		super(results);
	}
}
