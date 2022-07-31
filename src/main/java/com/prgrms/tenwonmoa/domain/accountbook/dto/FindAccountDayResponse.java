package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

@Getter
public class FindAccountDayResponse {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM", timezone = "Asia/Seoul")
	private final LocalDateTime registerDate;

	private final Long dayIncome;

	private final Long dayExpenditure;

	private final List<DayIncome> incomes;

	private final List<DayExpenditure> expenditures;

	public FindAccountDayResponse(
		LocalDateTime registerDate,
		Long dayIncome,
		Long dayExpenditure,
		List<DayIncome> incomes,
		List<DayExpenditure> expenditures) {
		this.registerDate = registerDate;
		this.dayIncome = dayIncome;
		this.dayExpenditure = dayExpenditure;
		this.incomes = incomes;
		this.expenditures = expenditures;
	}
}
