package com.prgrms.tenwonmoa.domain.category.service;

import static com.google.common.base.Preconditions.*;
import static com.prgrms.tenwonmoa.exception.message.Message.*;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.exception.message.Message;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCategoryService {

	private final UserCategoryRepository userCategoryRepository;

	private final CategoryService categoryService;

	public Long register(User user, String catgoryType, String name) {
		Category savedCategory = categoryService.register(catgoryType, name);

		UserCategory userCategory = userCategoryRepository.save(new UserCategory(user, savedCategory));
		return userCategory.getId();
	}

	public String updateName(User authenticatedUser, Long userCategoryId, String name) {
		UserCategory userCategory = getById(userCategoryId);

		User user = userCategory.getUser();
		validateUser(authenticatedUser, user);

		Category category = userCategory.getCategory();
		category.updateName(name);
		return category.getName();
	}

	private void validateUser(User authenticatedUser, User categoryUser) {
		checkState(categoryUser.getId().equals(authenticatedUser.getId()),
			Message.CATEGORY_NO_AUTHENTICATION.getMessage());
	}

	@Transactional(readOnly = true)
	public UserCategory getById(Long userCategoryId) {
		return userCategoryRepository.findById(userCategoryId)
			.orElseThrow(() -> new NoSuchElementException(Message.USER_CATEGORY_NOT_FOUND.getMessage()));
	}

	private UserCategory getUserCategory(User user, Long categoryId) {
		return userCategoryRepository.findByUserAndCategory(user.getId(), categoryId)
			.orElseThrow(() -> new NoSuchElementException(USER_CATEGORY_NOT_FOUND.getMessage()));
		// 이것도 정상이 아닌 공격 or 버그
		// -> 개발자가 알아야 할 예외(클라이언트에게는 잘못된 요청이라고 주고, 우리가 알아야 할 예외임)
	}
}
