package com.prgrms.tenwonmoa.domain.category;

import static com.google.common.base.Preconditions.*;
import static javax.persistence.EnumType.*;
import static lombok.AccessLevel.*;
import static org.springframework.util.StringUtils.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.prgrms.tenwonmoa.domain.common.BaseEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "category")
public class Category extends BaseEntity {

	public static final int MAX_NAME_LENGTH = 20;

	@Column(nullable = false, length = MAX_NAME_LENGTH)
	private String name;

	@Column(name = "category_kind")
	@Enumerated(STRING)
	private CategoryType categoryType;

	public Category(String name, CategoryType categoryType) {
		checkArgument(hasText(name));
		checkArgument(name.length() <= MAX_NAME_LENGTH);

		this.name = name;
		this.categoryType = categoryType;
	}
}
