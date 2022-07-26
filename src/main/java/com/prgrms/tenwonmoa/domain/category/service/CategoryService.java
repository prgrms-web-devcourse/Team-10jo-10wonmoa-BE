package com.prgrms.tenwonmoa.domain.category.service;

import static com.prgrms.tenwonmoa.exception.message.Message.*;

import java.util.Locale;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.dto.service.SingleCategoryResult;
import com.prgrms.tenwonmoa.domain.category.repository.CategoryRepository;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.domain.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

	private final CategoryRepository categoryRepository;

	private final UserCategoryRepository userCategoryRepository;

	public Long register(User user, String categoryType, String name) {
		CategoryType type = CategoryType.valueOf(categoryType.toUpperCase(Locale.ROOT));
		Category savedCategory = categoryRepository.save(new Category(name, type));

		userCategoryRepository.save(new UserCategory(user, savedCategory));

		return savedCategory.getId();
	}

	@Transactional(readOnly = true)
	public SingleCategoryResult getById(Long id) {
		return SingleCategoryResult.of(findCategoryById(id));
	}

	public String updateName(User user, Long categoryId, String name) {
		userCategoryRepository.findByUserAndCategory(user.getId(), categoryId)
			.orElseThrow(() -> new NoSuchElementException(USER_CATEGORY_NOT_FOUND.getMessage()));
		// 이것도 정상이 아닌 공격 or 버그
		// -> 개발자가 알아야 할 예외(클라이언트에게는 잘못된 요청이라고 주고, 우리가 알아야 할 예외임)

		Category category = findCategoryById(categoryId);
		category.updateName(name);
		categoryRepository.save(category);
		return name;
	}

	public void delete(User user, Long categoryId) {
		// 해당 categoryId를 user가 가지고 있는지 비즈니스 로직 검증
		// 카테고리 삭제
		// userCategory에서도 삭제
	}

	private Category findCategoryById(Long id) {
		return categoryRepository.findById(id).orElseThrow(
			() -> new NoSuchElementException(CATEGORY_NOT_FOUND.getMessage()));
		// 해당 예외는 공격인 것임. 없는 카테고리에 대한 조회는 정상적인 환경에서는 나올 수 가없음
	}
}
