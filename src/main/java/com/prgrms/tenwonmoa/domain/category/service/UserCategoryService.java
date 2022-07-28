package com.prgrms.tenwonmoa.domain.category.service;

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

	@Transactional(readOnly = true)
	public UserCategory getById(Long userCategoryId) {
		return userCategoryRepository.findById(userCategoryId)
			.orElseThrow(() -> new NoSuchElementException(Message.USER_CATEGORY_NOT_FOUND.getMessage()));
	}
}
