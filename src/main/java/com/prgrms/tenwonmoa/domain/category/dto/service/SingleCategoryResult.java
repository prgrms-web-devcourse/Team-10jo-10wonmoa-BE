package com.prgrms.tenwonmoa.domain.category.dto.service;

import com.prgrms.tenwonmoa.domain.category.Category;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class SingleCategoryResult {
	private final Long id;

	private final String name;

	private final String type;

	public static SingleCategoryResult of(Category category) {
		return new SingleCategoryResult(category.getId(), category.getName(), category.getCategoryType().name());
	}
}
