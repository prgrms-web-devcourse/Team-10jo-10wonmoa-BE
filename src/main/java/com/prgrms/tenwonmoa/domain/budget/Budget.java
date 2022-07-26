package com.prgrms.tenwonmoa.domain.budget;

import static com.google.common.base.Preconditions.*;
import static com.prgrms.tenwonmoa.domain.accountbook.AccountBookConst.*;
import static com.prgrms.tenwonmoa.exception.message.Message.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import java.text.MessageFormat;
import java.time.YearMonth;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.prgrms.tenwonmoa.domain.budget.converter.YearMonthIntegerAttributeConverter;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.common.BaseEntity;
import com.prgrms.tenwonmoa.domain.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "budget")
public class Budget extends BaseEntity {
	private static final int BUDGET_AMOUNT_MIN = 0;
	private static final String INVALID_BUDGET_AMOUNT_ERR_MSG = MessageFormat.format("입력 가능 범위는 {0}~{1}입니다.",
		BUDGET_AMOUNT_MIN, AMOUNT_MAX);
	@Column(name = "amount", nullable = false)
	private Long amount;

	@Column(name = "register_date", nullable = false, columnDefinition = "mediumint")
	@Convert(converter = YearMonthIntegerAttributeConverter.class)
	private YearMonth registerDate;

	@ManyToOne(fetch = LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_category_id", nullable = false)
	private UserCategory userCategory;

	public Budget(Long amount, YearMonth registerDate, User user, UserCategory userCategory) {
		validateRegisterDate(registerDate);
		validateAmount(amount);
		validateUser(user);
		validateUserCategory(userCategory);
		this.registerDate = registerDate;
		this.amount = amount;
		this.user = user;
		this.userCategory = userCategory;
	}

	public String getCategoryName() {
		return this.userCategory.getCategoryName();
	}

	public void changeAmount(Long amount) {
		validateAmount(amount);
		this.amount = amount;
	}

	public void validateOwner(Long authId) {
		this.user.validateLoginUser(authId);
	}

	private void validateUserCategory(UserCategory userCategory) {
		checkArgument(userCategory != null, "유저카테고리가 존재해야 합니다.");
	}

	private void validateUser(User user) {
		checkArgument(user != null, "사용자가 존재해야 합니다.");
	}

	private void validateAmount(Long amount) {
		checkArgument(amount != null, NOT_NULL_AMOUNT.getMessage());
		checkArgument(amount >= BUDGET_AMOUNT_MIN && amount <= AMOUNT_MAX, INVALID_BUDGET_AMOUNT_ERR_MSG);
	}

	private void validateRegisterDate(YearMonth registerDate) {
		checkArgument(registerDate != null, NOT_NULL_REGISTER_DATE.getMessage());
	}

}
