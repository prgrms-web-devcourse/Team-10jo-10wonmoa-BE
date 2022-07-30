package com.prgrms.tenwonmoa.domain.category;

import static com.google.common.base.Preconditions.*;
import static javax.persistence.EnumType.*;
import static lombok.AccessLevel.*;
import static org.springframework.util.StringUtils.*;

import java.util.List;
import java.util.Map;

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

	public static final Map<CategoryType, List<String>> DEFAULT_CATEGORY = Map.of(
		CategoryType.EXPENDITURE, List.of("교통/차량", "문화생활",
			"마트/편의점", "패션/미용", "생활용품", "주거/통신",
			"건강", "교육", "경조사/회비", "부모님", "기타"),
		CategoryType.INCOME, List.of("월급", "부수입", "용돈", "상여", "금융소득", "기타")
	);
	public static final int MAX_NAME_LENGTH = 20;

	@Column(nullable = false, length = MAX_NAME_LENGTH)
	private String name;

	@Column(name = "category_kind")
	@Enumerated(STRING)
	private CategoryType categoryType;

	public Category(String name, CategoryType categoryType) {
		validateName(name);

		this.name = name;
		this.categoryType = categoryType;
	}

	public void updateName(String name) {
		validateName(name);
		this.name = name;
	}

	private void validateName(String name) {
		checkArgument(hasText(name));
		checkArgument(name.length() <= MAX_NAME_LENGTH);
	}
}
