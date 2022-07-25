package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.time.LocalDate;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;

public class CreateExpenditureRequest {

	private final LocalDate registerDate;

	private final Long amount;

	private final String content;

	private final Long userCategoryId;

	public CreateExpenditureRequest(LocalDate registerDate, Long amount, String content, Long userCategoryId) {
		this.registerDate = registerDate;
		this.amount = amount;
		this.content = content;
		this.userCategoryId = userCategoryId;
	}

	public Expenditure toEntity(User user, UserCategory userCategory, String categoryName) {
		return new Expenditure(
			this.registerDate,
			this.amount,
			this.content,
			categoryName,
			user,
			userCategory
		);
	}

	public Long getUserCategoryId() {
		return userCategoryId;
	}
}
