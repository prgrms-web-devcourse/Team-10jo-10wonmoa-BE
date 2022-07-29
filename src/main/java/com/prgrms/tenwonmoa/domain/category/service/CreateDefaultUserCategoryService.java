package com.prgrms.tenwonmoa.domain.category.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateDefaultUserCategoryService {

	private final UserCategoryService userCategoryService;

	public void createDefaultUserCategory(User user) {
		for (Map.Entry<CategoryType, List<String>> entry : Category.DEFAULT_CATEGORY.entrySet()) {
			CategoryType categoryType = entry.getKey();
			List<String> categoryNames = entry.getValue();
			for (String categoryName : categoryNames) {
				userCategoryService.createUserCategory(user, categoryType.name(), categoryName);
			}
		}
	}
}
