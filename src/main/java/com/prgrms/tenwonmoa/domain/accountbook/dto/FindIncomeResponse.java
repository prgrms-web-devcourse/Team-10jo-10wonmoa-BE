package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.time.LocalDate;

import com.prgrms.tenwonmoa.domain.accountbook.Income;

import lombok.Getter;

@Getter
public class FindIncomeResponse {

	private final Long id;

	private final LocalDate registerDate;

	private final Long amount;

	private final String content;

	private final String categoryName;

	public FindIncomeResponse(Long id, LocalDate registerDate, Long amount, String content,
		String categoryName) {
		this.id = id;
		this.registerDate = registerDate;
		this.amount = amount;
		this.content = content;
		this.categoryName = categoryName;
	}

	public static FindIncomeResponse of(Income income) {
		return new FindIncomeResponse(
			income.getId(),
			income.getRegisterDate(),
			income.getAmount(),
			income.getContent(),
			income.getCategoryName()
		);
	}
}
