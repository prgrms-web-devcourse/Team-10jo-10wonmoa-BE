package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.time.LocalDate;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;

public class FindExpenditureResponse {

	private final LocalDate registerDate;

	private final Long amount;

	private final String content;

	private final Long userCategoryId;

	private FindExpenditureResponse(LocalDate registerDate, Long amount, String content, Long userCategoryId) {
		this.registerDate = registerDate;
		this.amount = amount;
		this.content = content;
		this.userCategoryId = userCategoryId;
	}

	public static FindExpenditureResponse of(Expenditure expenditure) {
		return new FindExpenditureResponse(
			expenditure.getRegisterDate(),
			expenditure.getAmount(),
			expenditure.getContent(),
			expenditure.getUserCategory().getId()
		);
	}
}
