package com.prgrms.tenwonmoa.domain.budget;

import static com.google.common.base.Preconditions.*;
import static com.prgrms.tenwonmoa.domain.accountbook.AccountBookConst.*;
import static com.prgrms.tenwonmoa.exception.message.Message.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
	@Column(name = "amount", nullable = false)
	private Long amount;

	@Column(name = "register_date", nullable = false)
	private LocalDate registerDate;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "user_category_id", nullable = false)
	private UserCategory userCategory;
	public Budget(Long amount, LocalDate registerDate, User user, UserCategory userCategory) {
		validateRegisterDate(registerDate);
		validateAmount(amount);
		checkArgument(user != null, "사용자가 존재해야 합니다.");
		checkArgument(userCategory != null, "유저카테고리가 존재해야 합니다.");
		this.registerDate = registerDate;
		this.amount = amount;
		this.user = user;
		this.userCategory = userCategory;
	}
	public void changeUserCategory(UserCategory userCategory) {
		this.userCategory = userCategory;
	}

	private void validateAmount(Long amount) {
		checkArgument(amount != null, NOT_NULL_AMOUNT.getMessage());
		checkArgument(amount >= AMOUNT_MIN && amount <= AMOUNT_MAX, INVALID_AMOUNT_ERR_MSG.getMessage());
	}

	private void validateRegisterDate(LocalDate registerDate) {
		checkArgument(registerDate != null, NOT_NULL_REGISTER_DATE.getMessage());
	}

}
