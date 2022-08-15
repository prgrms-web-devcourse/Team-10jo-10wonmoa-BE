package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

@Getter
public class FindDayAccountResponse implements Comparable<FindDayAccountResponse> {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private final LocalDate registerDate;

	private final Long incomeSum;

	private final Long expenditureSum;

	private final List<AccountBookItem> dayDetails;

	public FindDayAccountResponse(
		LocalDate registerDate,
		Long incomeSum,
		Long expenditureSum,
		List<AccountBookItem> dayDetails) {
		this.registerDate = registerDate;
		this.incomeSum = incomeSum;
		this.expenditureSum = expenditureSum;
		this.dayDetails = dayDetails;
	}

	/**
	 * 날짜 최신순으로 정렬
	 * */
	@Override
	public int compareTo(FindDayAccountResponse response) {
		return this.registerDate.isAfter(response.getRegisterDate()) ? -1 : 1;
	}
}
