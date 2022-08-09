package com.prgrms.tenwonmoa.domain.budget.dto;

import java.time.YearMonth;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.prgrms.tenwonmoa.domain.budget.Budget;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;

import lombok.Getter;

@Getter
public class CreateOrUpdateBudgetRequest {
	@Min(value = 0L, message = "최소값은 0입니다")
	@Max(value = 1_000_000_000_000L, message = "최대값은 1조입니다")
	private Long amount;
	@NotNull(message = "등록일을 채워주세요")
	private YearMonth registerDate;
	@NotNull(message = "유저카테고리 정보를 채워주세요")
	private Long userCategoryId;

	public CreateOrUpdateBudgetRequest(Long amount, YearMonth registerDate, Long userCategoryId) {
		this.amount = amount;
		this.registerDate = registerDate;
		this.userCategoryId = userCategoryId;
	}

	public Budget toEntity(User authUser, UserCategory userCategory) {
		return new Budget(
			this.amount,
			this.registerDate,
			authUser,
			userCategory
		);
	}
}
