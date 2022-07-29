package com.prgrms.tenwonmoa.domain.category.dto.service;

import java.util.List;
import java.util.stream.Collectors;

import com.prgrms.tenwonmoa.domain.category.Category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Getter
public final class ReadCategoryResult {

	private final List<SingleCategoryResult> categories;

	public static ReadCategoryResult of(List<Category> categories) {
		List<SingleCategoryResult> categoryResults = categories.stream()
			.map(category -> new SingleCategoryResult(
				category.getId(), category.getName(), category.getCategoryType().name()))
			.collect(Collectors.toList());

		return new ReadCategoryResult(categoryResults);
	}

	@RequiredArgsConstructor
	@Getter
	private static class SingleCategoryResult {

		private final Long id;

		private final String name;

		private final String categoryType;

	}
}
