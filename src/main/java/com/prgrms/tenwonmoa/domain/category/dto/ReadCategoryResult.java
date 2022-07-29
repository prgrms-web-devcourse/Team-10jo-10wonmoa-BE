package com.prgrms.tenwonmoa.domain.category.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.prgrms.tenwonmoa.domain.category.UserCategory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Getter
public final class ReadCategoryResult {

	private final List<SingleCategoryResult> categories;

	public static ReadCategoryResult of(List<UserCategory> categories) {
		List<SingleCategoryResult> categoryResults = categories.stream()
			.map(userCategory -> new SingleCategoryResult(
				userCategory.getId(), userCategory.getCategoryName(),
				userCategory.getCategoryType().name()))
			.collect(Collectors.toList());

		return new ReadCategoryResult(categoryResults);
	}

	@RequiredArgsConstructor
	@Getter
	public static class SingleCategoryResult {

		private final Long id;

		private final String name;

		private final String categoryType;

	}
}
