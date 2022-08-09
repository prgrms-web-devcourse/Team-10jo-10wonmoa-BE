package com.prgrms.tenwonmoa.domain.accountbook.dto.expenditure;

import java.time.LocalDateTime;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;

import lombok.Getter;

@Getter
public class FindExpenditureResponse {

	private final Long id;

	private final LocalDateTime registerDate;

	private final Long amount;

	private final String content;

	private final Long userCategoryId;

	private final String categoryName;

	public FindExpenditureResponse(
		Long id,
		LocalDateTime registerDate,
		Long amount,
		String content,
		Long userCategoryId,
		String categoryName
	) {
		this.id = id;
		this.registerDate = registerDate;
		this.amount = amount;
		this.content = content;
		this.userCategoryId = userCategoryId;
		this.categoryName = categoryName;
	}

	public static FindExpenditureResponse of(Expenditure expenditure) {
		return new FindExpenditureResponse(
			expenditure.getId(),
			expenditure.getRegisterDate(),
			expenditure.getAmount(),
			expenditure.getContent(),
			expenditure.getUserCategoryId(),
			expenditure.getCategoryName()
		);
	}
}
