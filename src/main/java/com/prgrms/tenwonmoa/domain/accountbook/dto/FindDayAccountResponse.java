package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

@Getter
public class FindDayAccountResponse {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private final LocalDate registerDate;

	private final Long dayIncome;

	private final Long dayExpenditure;

	private final List<DayDetail> dayDetails;

	public FindDayAccountResponse(
		LocalDate registerDate,
		Long dayIncome,
		Long dayExpenditure,
		List<DayDetail> dayDetails) {
		this.registerDate = registerDate;
		this.dayIncome = dayIncome;
		this.dayExpenditure = dayExpenditure;
		this.dayDetails = dayDetails;
	}
}
