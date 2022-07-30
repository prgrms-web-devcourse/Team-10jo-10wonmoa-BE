package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;

import lombok.Getter;

@Getter
public class CreateIncomeRequest {
	@NotNull
	private final LocalDateTime registerDate;

	@Min(1L)
	@Max(1_000_000_000_000L)
	private final Long amount;

	@Size(max = 50)
	private final String content;

	@NotNull
	private final Long userCategoryId;

	public CreateIncomeRequest(LocalDateTime registerDate, Long amount, String content, Long userCategoryId) {
		this.registerDate = registerDate;
		this.amount = amount;
		this.content = content;
		this.userCategoryId = userCategoryId;
	}

	public Income toEntity(User user, UserCategory userCategory, String categoryName) {
		return new Income(
			this.registerDate,
			this.amount,
			this.content,
			categoryName,
			user,
			userCategory
		);
	}
}
