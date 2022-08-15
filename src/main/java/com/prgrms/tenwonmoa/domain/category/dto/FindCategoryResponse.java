package com.prgrms.tenwonmoa.domain.category.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.prgrms.tenwonmoa.domain.category.UserCategory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Getter
public final class FindCategoryResponse {

	private final List<SingleCategoryResponse> categories;

	public static FindCategoryResponse of(List<UserCategory> categories) {
		List<SingleCategoryResponse> categoryResults = categories.stream()
			.map(userCategory -> new SingleCategoryResponse(
				userCategory.getId(), userCategory.getCategoryName(),
				userCategory.getCategoryTypeName()))
			.collect(Collectors.toList());

		return new FindCategoryResponse(categoryResults);
	}

	@RequiredArgsConstructor
	@Getter
	public static class SingleCategoryResponse {

		private final Long id;

		private final String name;

		private final String categoryType;

	}
}
