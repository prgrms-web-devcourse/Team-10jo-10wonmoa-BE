package com.prgrms.tenwonmoa.domain.accountbook.service;

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
import com.prgrms.tenwonmoa.exception.message.Message;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ExpenditureService {
	private final UserCategoryRepository userCategoryRepository;
	private final CategoryRepository categoryRepository;
	private final ExpenditureRepository expenditureRepository;

	public CreateExpenditureResponse createExpenditure(Long authenticatedUserId,
		CreateExpenditureRequest createExpenditureRequest) {
		UserCategory userCategory = getUserCategory(createExpenditureRequest.getUserCategoryId());
		Category category = getCategory(userCategory.getCategory().getId());

		User categoryUser = userCategory.getUser();
		categoryUser.validateLoginUser(authenticatedUserId);

		Expenditure expenditure = createExpenditureRequest.toEntity(categoryUser, userCategory,
			category.getName());
		return CreateExpenditureResponse.of(expenditureRepository.save(expenditure));
	}

	public void updateExpenditure(Long authenticatedUserId, Long expenditureId,
		UpdateExpenditureRequest updateExpenditureRequest) {
		Expenditure expenditure = getExpenditure(expenditureId);
		User expenditureUser = expenditure.getUser();

		expenditureUser.validateLoginUser(authenticatedUserId);

		// 변경하려는 userCategory
		UserCategory userCategory = getUserCategory(updateExpenditureRequest.getUserCategoryId());

		expenditure.update(userCategory, updateExpenditureRequest);
	}

	public FindExpenditureResponse findExpenditure(Long authenticatedUserId, Long expenditureId) {
		Expenditure expenditure = getExpenditure(expenditureId);

		User expenditureUser = expenditure.getUser();
		expenditureUser.validateLoginUser(authenticatedUserId);

		return FindExpenditureResponse.of(expenditure);
	}

	public void deleteExpenditure(Long authenticatedUserId, Long expenditureId) {
		Expenditure expenditure = getExpenditure(expenditureId);

		User expenditureUser = expenditure.getUser();
		expenditureUser.validateLoginUser(authenticatedUserId);

		expenditureRepository.delete(expenditure);
	}

	public void setUserCategoryNull(Long userCategoryId) {
		expenditureRepository.updateUserCategoryAsNull(userCategoryId);
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
