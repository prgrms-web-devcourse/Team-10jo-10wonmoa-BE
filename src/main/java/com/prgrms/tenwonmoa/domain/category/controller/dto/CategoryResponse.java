package com.prgrms.tenwonmoa.domain.category.controller.dto;

import com.prgrms.tenwonmoa.domain.category.Category;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class CategoryResponse {

	@AllArgsConstructor
	@Getter
	public static class SingleCategoryResponse{
		private Long id;

		private String name;

		private String type;

		public static SingleCategoryResponse of(Category category){
			return new SingleCategoryResponse(category.getId(), category.getName(), category.getCategoryType().name());
		}
	}
}
