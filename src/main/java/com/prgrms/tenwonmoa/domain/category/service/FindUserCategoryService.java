package com.prgrms.tenwonmoa.domain.category.service;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.dto.FindCategoryResponse;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindUserCategoryService {

	private final UserCategoryRepository repository;

	public FindCategoryResponse getUserCategories(Long userId, String categoryType) {
		CategoryType type = CategoryType.valueOf(categoryType.toUpperCase(Locale.ROOT));

		List<UserCategory> userCategories = repository.findByUserIdAndCategoryType(userId, type);

		return FindCategoryResponse.of(userCategories);
	}
}
