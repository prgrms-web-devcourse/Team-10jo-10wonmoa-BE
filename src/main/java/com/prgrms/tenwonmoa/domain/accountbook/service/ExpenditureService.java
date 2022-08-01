package com.prgrms.tenwonmoa.domain.accountbook.service;

import static com.google.common.base.Preconditions.*;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.accountbook.dto.expenditure.CreateExpenditureRequest;
import com.prgrms.tenwonmoa.domain.accountbook.dto.expenditure.CreateExpenditureResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.expenditure.FindExpenditureResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.expenditure.UpdateExpenditureRequest;
import com.prgrms.tenwonmoa.domain.accountbook.repository.ExpenditureRepository;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.repository.CategoryRepository;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;
import com.prgrms.tenwonmoa.exception.message.Message;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ExpenditureService {

	private final UserRepository userRepository;
	private final UserCategoryRepository userCategoryRepository;
	private final CategoryRepository categoryRepository;
	private final ExpenditureRepository expenditureRepository;

	public CreateExpenditureResponse createExpenditure(Long userId, CreateExpenditureRequest createExpenditureRequest) {
		User user = getUser(userId);
		UserCategory userCategory = getUserCategory(createExpenditureRequest.getUserCategoryId());
		Category category = getCategory(userCategory.getCategory().getId());
		Expenditure expenditure = createExpenditureRequest.toEntity(user, userCategory, category.getName());

		Expenditure savedExpenditure = expenditureRepository.save(expenditure);

		return CreateExpenditureResponse.of(savedExpenditure);
	}

	public void updateExpenditure(Long userId, Long expenditureId, UpdateExpenditureRequest updateExpenditureRequest) {
		User currentUser = getUser(userId);
		Expenditure expenditure = getExpenditure(expenditureId);
		validateUser(currentUser, expenditure.getUser());

		// 변경하려는 userCategory
		UserCategory userCategory = getUserCategory(updateExpenditureRequest.getUserCategoryId());

		expenditure.update(userCategory, updateExpenditureRequest);
	}

	public FindExpenditureResponse findExpenditure(Long userId, Long expenditureId) {
		User user = getUser(userId);
		Expenditure expenditure = getExpenditure(expenditureId);

		validateUser(user, expenditure.getUser());

		return FindExpenditureResponse.of(expenditure);
	}

	public void deleteExpenditure(Long userId, Long expenditureId) {
		User user = getUser(userId);
		Expenditure expenditure = getExpenditure(expenditureId);

		validateUser(user, expenditure.getUser());

		expenditureRepository.delete(expenditure);
	}

	public void setUserCategoryNull(Long userCategoryId) {
		expenditureRepository.updateUserCategoryAsNull(userCategoryId);
	}

	private void validateUser(User currentUser, User expenditureUser) {
		checkState(currentUser == expenditureUser, Message.EXPENDITURE_NO_AUTHENTICATION.getMessage());
	}

	private User getUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new NoSuchElementException(Message.USER_NOT_FOUND.getMessage()));
	}

	private UserCategory getUserCategory(Long userCategoryId) {
		return userCategoryRepository.findById(userCategoryId)
			.orElseThrow(() -> new NoSuchElementException(Message.USER_CATEGORY_NOT_FOUND.getMessage()));
	}

	private Category getCategory(Long categoryId) {
		return categoryRepository.findById(categoryId)
			.orElseThrow(() -> new NoSuchElementException(Message.CATEGORY_NOT_FOUND.getMessage()));
	}

	private Expenditure getExpenditure(Long expenditureId) {
		return expenditureRepository.findById(expenditureId)
			.orElseThrow(() -> new NoSuchElementException(Message.EXPENDITURE_NOT_FOUND.getMessage()));
	}
}
