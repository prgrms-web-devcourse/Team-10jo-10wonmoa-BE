package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.time.LocalDate;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;

public class FindExpenditureResponse {

	private final Long id;

	private final LocalDate registerDate;

	private final Long amount;

	private final String content;

	private final Long categoryId;

	private final String categoryName;

	private FindExpenditureResponse(Long id, LocalDate registerDate, Long amount, String content, Long categoryId,
		String categoryName) {
		this.id = id;
		this.registerDate = registerDate;
		this.amount = amount;
		this.content = content;
		this.categoryId = categoryId;
		this.categoryName = categoryName;
	}

	public static FindExpenditureResponse of(Expenditure expenditure) {
		return new FindExpenditureResponse(
			expenditure.getId(),
			expenditure.getRegisterDate(),
			expenditure.getAmount(),
			expenditure.getContent(),
			expenditure.getUserCategory().getId(),
			expenditure.getCategoryName()
		);
	}
}
