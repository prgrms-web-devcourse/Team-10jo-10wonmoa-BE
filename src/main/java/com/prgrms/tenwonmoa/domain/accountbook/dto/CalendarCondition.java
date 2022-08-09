package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.time.LocalDate;
import java.util.Calendar;

public class CalendarCondition {

	private final LocalDate date;
	private static final Calendar CALENDAR_INSTANCE = Calendar.getInstance();

	public CalendarCondition(LocalDate date) {
		this.date = date;
		CALENDAR_INSTANCE.set(getYear(), getMonth() - 1, 1);
	}

	public int getYear() {
		return this.date.getYear();
	}

	public int getMonth() {
		return this.date.getMonthValue();
	}

	public int getLastDayOfMonth() {
		return CALENDAR_INSTANCE.getActualMaximum(CALENDAR_INSTANCE.DATE);
	}
}
