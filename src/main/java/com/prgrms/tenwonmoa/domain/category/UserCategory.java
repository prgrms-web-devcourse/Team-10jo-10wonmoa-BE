package com.prgrms.tenwonmoa.domain.category;

import static com.google.common.base.Preconditions.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.prgrms.tenwonmoa.domain.common.BaseEntity;
import com.prgrms.tenwonmoa.domain.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

/* 유저가 가지고 있는 카테고리*/
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "user_category")
@Getter
public class UserCategory extends BaseEntity {

	@ManyToOne(fetch = EAGER)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = EAGER, optional = false)
	@JoinColumn(name = "category_id")
	private Category category;

	public UserCategory(User user, Category category) {
		checkArgument(user != null);
		checkArgument(category != null);

		this.user = user;
		this.category = category;
	}

	public String getCategoryName() {
		return this.category.getName();
	}

	public CategoryType getCategoryType() {
		return this.category.getCategoryType();
	}
}
