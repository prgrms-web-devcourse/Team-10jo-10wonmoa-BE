package com.prgrms.tenwonmoa.domain.accountbook.dto;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;

public class CreateExpenditureResponse {

	private final Long id;

	public CreateExpenditureResponse(Long id) {
		this.id = id;
	}

	public static CreateExpenditureResponse of(Expenditure expenditure) {
		return new CreateExpenditureResponse(expenditure.getId());
	}
}
