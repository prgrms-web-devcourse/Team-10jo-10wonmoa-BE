package com.prgrms.tenwonmoa.domain.accountbook;

import static com.google.common.base.Preconditions.*;
import static com.prgrms.tenwonmoa.domain.accountbook.AccountBookConst.*;
import static com.prgrms.tenwonmoa.exception.message.Message.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;
import static org.springframework.util.StringUtils.*;

import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.common.BaseEntity;
import com.prgrms.tenwonmoa.domain.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "income")
public class Income extends BaseEntity {
	@Column(name = "register_date", nullable = false)
	private LocalDate registerDate;

	@Column(name = "amount", nullable = false)
	private Long amount;

	@Column(name = "content", length = CONTENT_MAX)
	private String content;

	@Column(name = "category_name", nullable = false, length = Category.MAX_NAME_LENGTH)
	private String categoryName;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "user_category_id")
	private UserCategory userCategory;

	public Income(LocalDate registerDate, Long amount, String content, String categoryName, User user,
		UserCategory userCategory) {
		checkArgument(registerDate != null, NOT_NULL_REGISTER_DATE.getMessage());
		validateCategoryName(categoryName);
		validateAmount(amount);
		if (Objects.nonNull(content)) {
			checkArgument(content.length() <= CONTENT_MAX, INVALID_CONTENT_ERR_MSG.getMessage());
		}
		this.registerDate = LocalDate.now();
		this.amount = amount;
		this.content = content;
		this.categoryName = categoryName;
		this.user = user;
		this.userCategory = userCategory;
	}

	private void validateAmount(Long amount) {
		checkArgument(amount != null, NOT_NULL_AMOUNT.getMessage());
		checkArgument(amount >= AMOUNT_MIN && amount <= AMOUNT_MAX, INVALID_AMOUNT_ERR_MSG.getMessage());
	}

	private void validateCategoryName(String categoryName) {
		checkArgument(hasText(categoryName));
		checkArgument(categoryName.length() <= Category.MAX_NAME_LENGTH);
	}
}
