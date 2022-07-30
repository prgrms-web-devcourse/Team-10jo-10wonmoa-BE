package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;

@Getter
public class FindAccountDayResponse {

	private final LocalDateTime registerDate;

	private final Long dayIncome;

	private final List<DayDetail> details;

	public FindAccountDayResponse(LocalDateTime registerDate, Long dayIncome,
		List<DayDetail> details) {
		this.registerDate = registerDate;
		this.dayIncome = dayIncome;
		this.details = details;
	}
}
