package com.prgrms.tenwonmoa.domain.category.service;

import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.service.CategoryResult.SingleCategoryResult;
import com.prgrms.tenwonmoa.domain.category.repository.CategoryRepository;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.domain.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public final UserCategoryRepository userCategoryRepository;

	public SingleCategoryResult register(User user, String categoryType, String name) {
		CategoryType type = CategoryType.valueOf(categoryType.toUpperCase(Locale.ROOT));
		Category savedCategory = categoryRepository.save(new Category(name, type));

		userCategoryRepository.save(new UserCategory(user, savedCategory));

		return SingleCategoryResult.of(savedCategory);
	}

	public SingleCategoryResult updateName(User user, Long categoryId, String name) {
		// 해당 categoryId를 user가 가지고 있는지 비즈니스 로직 검증
		// 카테고리 찾아오기
		// 이름 업데이트
		return null;
	}

	public void delete(User user, Long categoryId) {
		// 해당 categoryId를 user가 가지고 있는지 비즈니스 로직 검증
		// 카테고리 삭제
		// userCategory에서도 삭제
	}
}
