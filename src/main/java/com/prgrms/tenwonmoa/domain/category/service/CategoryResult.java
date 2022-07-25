package com.prgrms.tenwonmoa.domain.category.service;

import com.prgrms.tenwonmoa.domain.category.Category;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class CategoryResult {

	@AllArgsConstructor
	@Getter
	public static class SingleCategoryResult {
		private Long id;

		private String name;

		private String type;

		public static SingleCategoryResult of(Category category){
			return new SingleCategoryResult(category.getId(), category.getName(), category.getCategoryType().name());
		}
	}
}
