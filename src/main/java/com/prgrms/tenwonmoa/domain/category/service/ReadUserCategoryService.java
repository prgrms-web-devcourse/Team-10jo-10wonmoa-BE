package com.prgrms.tenwonmoa.domain.category.service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.dto.service.ReadCategoryResult;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReadUserCategoryService {

	private final UserCategoryRepository repository;

	public ReadCategoryResult getUserCategories(Long userId, String categoryType) {
		CategoryType type = CategoryType.valueOf(categoryType.toUpperCase(Locale.ROOT));

		List<Category> categories = repository.findByUserId(userId)
			.stream().map(UserCategory::getCategory)
			.filter(category -> category.getCategoryType().equals(type))
			.collect(Collectors.toList());

		return ReadCategoryResult.of(categories);
	}
}
