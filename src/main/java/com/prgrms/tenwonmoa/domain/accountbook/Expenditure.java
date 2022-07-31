package com.prgrms.tenwonmoa.domain.accountbook;

import static com.google.common.base.Preconditions.*;
import static com.prgrms.tenwonmoa.domain.accountbook.AccountBookConst.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.prgrms.tenwonmoa.domain.accountbook.dto.UpdateExpenditureRequest;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.common.BaseEntity;
import com.prgrms.tenwonmoa.domain.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "expenditure")
@Getter
public class Expenditure extends BaseEntity {

	@Column(name = "register_date", nullable = false)
	private LocalDateTime registerDate;

	@Column(name = "amount", nullable = false)
	private Long amount;

	@Column(name = "content", nullable = true, length = CONTENT_MAX)
	private String content;

	@Column(name = "category_name", nullable = false, length = Category.MAX_NAME_LENGTH)
	private String categoryName;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(
		foreignKey = @ForeignKey(name = "fk_expenditure_user"),
		name = "user_id")
	private User user;

	@OneToOne(fetch = LAZY)
	@JoinColumn(
		foreignKey = @ForeignKey(name = "fk_expenditure_user_category"),
		name = "user_category_id")
	private UserCategory userCategory;

	public Expenditure(LocalDateTime registerDate, Long amount, String content,
		String categoryName, User user, UserCategory userCategory) {
		checkArgument(registerDate != null, "날짜는 필수입니다.");
		validateAmount(amount);
		validateCategoryName(categoryName);
		checkArgument(user != null, "사용자가 존재해야 합니다.");
		checkArgument(userCategory != null, "분류는 필수입니다.");
		this.registerDate = registerDate;
		this.amount = amount;
		this.content = content;
		this.categoryName = categoryName;
		this.user = user;
		this.userCategory = userCategory;
	}

	public void update(UserCategory userCategory, UpdateExpenditureRequest request) {
		this.userCategory = userCategory;
		this.registerDate = request.getRegisterDate();
		this.amount = request.getAmount();
		this.content = request.getContent();
	}

	private void validateCategoryName(String categoryName) {
		checkArgument(categoryName != null, "분류이름은 필수 입니다.");
		checkArgument(!categoryName.isBlank(), "분류이름은 공백일 수 없습니다.");
	}

	private void validateAmount(Long amount) {
		checkArgument(amount != null, "금액은 필수입니다.");
		checkArgument(amount >= AMOUNT_MIN && amount <= AMOUNT_MAX,
			"입력할 수 있는 범위가 아닙니다.");
	}

	/**
	 * TODO : naming 부자연스럽지 않은지 조언 필요
	 * */
	public String getCategoryName() {
		if (Objects.isNull(this.userCategory)) {
			return this.categoryName;
		}

		return this.userCategory.getCategory().getName();
	}

	/**
	 * TODO : 의논 필요, 윌리엄이 userCategory 만들때 null 설정할 메서드
	 * */
	public void deleteUserCategory() {
		this.userCategory = null;
	}
}
