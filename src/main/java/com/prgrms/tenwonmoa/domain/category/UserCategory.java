package com.prgrms.tenwonmoa.domain.category;

import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.prgrms.tenwonmoa.domain.common.BaseEntity;
import com.prgrms.tenwonmoa.domain.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
public class UserCategory extends BaseEntity {

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = LAZY, optional = false)
	@JoinColumn(name = "category_id")
	private Category category;

	public UserCategory(User user, Category category) {
		this.user = user;
		this.category = category;
	}
}
