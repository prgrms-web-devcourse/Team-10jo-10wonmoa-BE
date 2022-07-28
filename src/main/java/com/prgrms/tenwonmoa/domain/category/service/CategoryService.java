package com.prgrms.tenwonmoa.domain.category.service;

import static com.prgrms.tenwonmoa.exception.message.Message.*;

import java.util.Locale;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.repository.CategoryRepository;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

	private final CategoryRepository categoryRepository;

	private final UserCategoryRepository userCategoryRepository;

	Category register(String categoryType, String name) {
		CategoryType type = CategoryType.valueOf(categoryType.toUpperCase(Locale.ROOT));
		return categoryRepository.save(new Category(name, type));
	}

	@Transactional(readOnly = true)
	Category getById(Long id) {
		return categoryRepository.findById(id).orElseThrow(
			() -> new NoSuchElementException(CATEGORY_NOT_FOUND.getMessage()));
		// 해당 예외는 공격인 것임. 없는 카테고리에 대한 조회는 정상적인 환경에서는 나올 수 가없음
	}

	public void delete(Long categoryId) {
		Category category = getById(categoryId);

		categoryRepository.delete(category);
	}

}
