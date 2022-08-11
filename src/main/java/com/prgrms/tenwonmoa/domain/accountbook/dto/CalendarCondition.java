package com.prgrms.tenwonmoa.domain.accountbook.dto;

import static com.google.common.base.Preconditions.*;

import java.util.Calendar;

import com.prgrms.tenwonmoa.exception.message.Message;

public class CalendarCondition {

	private final int year;
	private final int month;
	private static final Calendar CALENDAR_INSTANCE = Calendar.getInstance();

	public CalendarCondition(int year, int month) {
		checkArgument(year >= 1900 && year <= 3000, Message.INVALID_YEAR.getMessage());
		checkArgument(month >= 1 && month <= 12, Message.INVALID_MONTH.getMessage());

		this.year = year;
		this.month = month;
		CALENDAR_INSTANCE.set(this.year, this.month - 1, 1);
	}

	public int getYear() {
		return this.year;
	}

	public int getMonth() {
		return this.month;
	}

	public int getLastDayOfMonth() {
		return CALENDAR_INSTANCE.getActualMaximum(CALENDAR_INSTANCE.DATE);
	}
}
