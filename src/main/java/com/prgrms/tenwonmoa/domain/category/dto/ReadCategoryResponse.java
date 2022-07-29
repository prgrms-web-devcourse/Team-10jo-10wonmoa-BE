package com.prgrms.tenwonmoa.domain.category.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.prgrms.tenwonmoa.domain.category.UserCategory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Getter
public final class ReadCategoryResponse {

	private final List<SingleCategoryResponse> categories;

	public static ReadCategoryResponse of(List<UserCategory> categories) {
		List<SingleCategoryResponse> categoryResults = categories.stream()
			.map(userCategory -> new SingleCategoryResponse(
				userCategory.getId(), userCategory.getCategoryName(),
				userCategory.getCategoryType().name()))
			.collect(Collectors.toList());

		return new ReadCategoryResponse(categoryResults);
	}

	@RequiredArgsConstructor
	@Getter
	public static class SingleCategoryResponse {

		private final Long id;

		private final String name;

		private final String categoryType;

	}
}
