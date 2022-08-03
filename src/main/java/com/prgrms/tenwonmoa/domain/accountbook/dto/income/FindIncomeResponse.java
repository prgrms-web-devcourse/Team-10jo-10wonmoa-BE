package com.prgrms.tenwonmoa.domain.accountbook.dto.income;

import java.time.LocalDateTime;

import com.prgrms.tenwonmoa.domain.accountbook.Income;

import lombok.Getter;

@Getter
public class FindIncomeResponse {

	private final Long id;

	private final LocalDateTime registerDate;

	private final Long amount;

	private final String content;

	private final Long userCategoryId;

	private final String categoryName;

	public FindIncomeResponse(Long id, LocalDateTime registerDate, Long amount, String content,
		Long userCategoryId, String categoryName) {
		this.id = id;
		this.registerDate = registerDate;
		this.amount = amount;
		this.content = content;
		this.userCategoryId = userCategoryId;
		this.categoryName = categoryName;
	}

	public static FindIncomeResponse of(Income income) {
		return new FindIncomeResponse(
			income.getId(),
			income.getRegisterDate(),
			income.getAmount(),
			income.getContent(),
			income.getUserCategory().getId(),
			income.getCategoryName()
		);
	}
}
