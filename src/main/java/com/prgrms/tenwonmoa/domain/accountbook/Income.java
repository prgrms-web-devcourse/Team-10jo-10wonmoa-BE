package com.prgrms.tenwonmoa.domain.accountbook;

import static com.google.common.base.Preconditions.*;
import static com.prgrms.tenwonmoa.domain.accountbook.AccountBookConst.*;
import static com.prgrms.tenwonmoa.exception.message.Message.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;
import static org.springframework.util.StringUtils.*;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.prgrms.tenwonmoa.domain.accountbook.dto.income.UpdateIncomeRequest;
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
	private LocalDateTime registerDate;

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

	public Income(LocalDateTime registerDate, Long amount, String content, String categoryName, User user,
		UserCategory userCategory) {
		validateRegisterDate(registerDate);
		validateCategoryName(categoryName);
		validateAmount(amount);
		validateContent(content);
		this.registerDate = registerDate;
		this.amount = amount;
		this.content = content;
		this.categoryName = categoryName;
		this.user = user;
		this.userCategory = userCategory;
	}

	public void update(UserCategory userCategory, UpdateIncomeRequest updateIncomeRequest) {
		this.userCategory = userCategory;
		changeCategoryName(userCategory.getCategory().getName());
		changeRegisterDate(updateIncomeRequest.getRegisterDate());
		changeAmount(updateIncomeRequest.getAmount());
		changeContent(updateIncomeRequest.getContent());
	}

	public void validateOwner(Long authId) {
		this.user.validateLogin(authId);
	}

	private void changeContent(String content) {
		validateContent(content);
		this.content = content;
	}

	private void changeRegisterDate(LocalDateTime registerDate) {
		validateRegisterDate(registerDate);
		this.registerDate = registerDate;
	}

	private void changeAmount(Long amount) {
		validateAmount(amount);
		this.amount = amount;
	}

	private void changeCategoryName(String categoryName) {
		validateCategoryName(categoryName);
		this.categoryName = categoryName;
	}

	private void validateContent(String content) {
		if (Objects.nonNull(content)) {
			checkArgument(content.length() <= CONTENT_MAX, INVALID_CONTENT_ERR_MSG.getMessage());
		}
	}

	private void validateRegisterDate(LocalDateTime registerDate) {
		checkArgument(registerDate != null, NOT_NULL_REGISTER_DATE.getMessage());
	}

	public String getCategoryName() {
		if (Objects.isNull(this.userCategory.getCategory())) {
			return this.categoryName;
		}

		return this.userCategory.getCategoryName();
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
