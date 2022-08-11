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
		user.validateLoginUser(authenticatedUser.getId());

		Category category = userCategory.getCategory();

		checkNotNull(category, String.format(
			"삭제된 카테고리는 수정할 수 없습니다, 유저 아이디 : %d, 유저 카테고리 아이디 : %d 업데이트할 이름 : %s",
			authenticatedUser.getId(), userCategoryId, desiredName));
		category.updateName(desiredName);

		return category.getName();
	}

	@Transactional(readOnly = true)
	public UserCategory findById(Long userCategoryId) {
		return userCategoryRepository.findById(userCategoryId)
			.orElseThrow(() -> new NoSuchElementException(Message.USER_CATEGORY_NOT_FOUND.getMessage()));
	}

	public void deleteUserCategory(User authenticatedUser, Long userCategoryId) {
		UserCategory userCategory = findById(userCategoryId);
		User user = userCategory.getUser();
		user.validateLoginUser(authenticatedUser.getId());

		Category category = userCategory.getCategory();
		userCategory.updateCategoryAsNull();

		checkNotNull(category, String.format(
			"삭제된 카테고리는 다시 삭제할 수 없습니다, 유저 아이디 : %d, 유저 카테고리 아이디 : %d",
			authenticatedUser.getId(), userCategoryId));

		makeCategoryNameUpToDate(userCategoryId, category.getName());

		categoryService.deleteCategory(category.getId());
	}

	private void makeCategoryNameUpToDate(Long userCategoryId, String categoryName) {
		incomeService.updateCategoryNameField(userCategoryId, categoryName);
		expenditureService.updateCategoryNameField(userCategoryId, categoryName);
	}
}
