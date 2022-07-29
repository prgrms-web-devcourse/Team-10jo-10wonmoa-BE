package com.prgrms.tenwonmoa.domain.category.service;

import static com.google.common.base.Preconditions.*;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.accountbook.service.ExpenditureService;
import com.prgrms.tenwonmoa.domain.accountbook.service.IncomeService;
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

	private final ExpenditureService expenditureService;

	private final IncomeService incomeService;

	public Long createUserCategory(User user, String catgoryType, String name) {
		Category savedCategory = categoryService.createCategory(catgoryType, name);

		UserCategory userCategory = userCategoryRepository.save(new UserCategory(user, savedCategory));
		return userCategory.getId();
	}

	public String updateName(User authenticatedUser, Long userCategoryId, String desiredName) {
		UserCategory userCategory = findById(userCategoryId);

		User user = userCategory.getUser();
		validateUser(authenticatedUser, user);

		Category category = userCategory.getCategory();
		category.updateName(desiredName);

		return category.getName();
	}

	private void validateUser(User authenticatedUser, User categoryUser) {
		checkState(categoryUser.getId().equals(authenticatedUser.getId()),
			Message.CATEGORY_NO_AUTHENTICATION.getMessage());
	}

	@Transactional(readOnly = true)
	public UserCategory findById(Long userCategoryId) {
		return userCategoryRepository.findById(userCategoryId)
			.orElseThrow(() -> new NoSuchElementException(Message.USER_CATEGORY_NOT_FOUND.getMessage()));
	}

	public void deleteUserCategory(User authenticatedUser, Long userCategoryId) {
		incomeService.setUserCategoryNull(userCategoryId);
		expenditureService.setUserCategoryNull(userCategoryId);

		UserCategory userCategory = findById(userCategoryId);
		User user = userCategory.getUser();
		validateUser(authenticatedUser, user);

		userCategoryRepository.delete(userCategory);
	}
}
