package com.prgrms.tenwonmoa.domain.accountbook.dto.expenditure;

import java.time.LocalDateTime;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;

import lombok.Getter;

@Getter
public class UpdateExpenditureRequest {

	@NotNull(message = "등록일을 채워주세요")
	private final LocalDateTime registerDate;

	@Min(value = 0L, message = "최소값은 0입니다")
	@Max(value = 1_000_000_000_000L, message = "최대값은 1조입니다")
	private final Long amount;

	@Size(max = 50, message = "내용의 최대 길이는 50입니다")
	private final String content;

	@NotNull(message = "유저 카테고리 아이디를 채워주세요")
	private final Long userCategoryId;

	public UpdateExpenditureRequest(LocalDateTime registerDate, Long amount, String content, Long userCategoryId) {
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
