package com.prgrms.tenwonmoa.domain.accountbook;

import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.common.BaseEntity;
import com.prgrms.tenwonmoa.domain.user.User;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "expenditure")
public class Expenditure extends BaseEntity {

	@Column(name = "register_date", nullable = false)
	private LocalDate registerDate;

	@Column(name = "amount", nullable = false)
	private Long amount;

	@Column(name = "content", nullable = false)
	private String content;

	@Column(name = "category_name", nullable = false)
	private String categoryName;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "user_category_id")
	private UserCategory userCategory;

	public Expenditure(LocalDate registerDate, Long amount, String content,
		String categoryName, User user, UserCategory userCategory) {
		this.registerDate = registerDate;
		this.amount = amount;
		this.content = content;
		this.categoryName = categoryName;
		this.user = user;
		this.userCategory = userCategory;
	}
}
