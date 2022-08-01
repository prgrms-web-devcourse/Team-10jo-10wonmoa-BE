package com.prgrms.tenwonmoa.domain.accountbook.dto.expenditure;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;

import lombok.Getter;

@Getter
public class CreateExpenditureResponse {

	private final Long id;

	private CreateExpenditureResponse(Long id) {
		this.id = id;
	}

	public static CreateExpenditureResponse of(Expenditure expenditure) {
		return new CreateExpenditureResponse(expenditure.getId());
	}
}
