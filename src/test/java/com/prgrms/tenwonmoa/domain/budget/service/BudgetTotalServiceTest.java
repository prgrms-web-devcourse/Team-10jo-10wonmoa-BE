package com.prgrms.tenwonmoa.domain.budget.service;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static com.prgrms.tenwonmoa.exception.message.Message.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.YearMonth;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prgrms.tenwonmoa.domain.budget.Budget;
import com.prgrms.tenwonmoa.domain.budget.dto.CreateOrUpdateBudgetRequest;
import com.prgrms.tenwonmoa.domain.budget.repository.BudgetRepository;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.service.UserCategoryService;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.service.UserService;

@DisplayName("예산 서비스 Total 테스트")
@ExtendWith(MockitoExtension.class)
class BudgetTotalServiceTest {

	@Mock
	private BudgetRepository budgetRepository;
	@Mock
	private UserService userService;
	@Mock
	private  UserCategoryService userCategoryService;
	@InjectMocks
	private BudgetTotalService budgetTotalService;

	private User user = createUser();
	private Category category = createExpenditureCategory();
	private UserCategory userCategory = createUserCategory(user, category);
	private Budget budget = new Budget(1000L, YearMonth.now(), user, userCategory);

	private CreateOrUpdateBudgetRequest createOrUpdateBudgetRequest = new CreateOrUpdateBudgetRequest(
		1000L, YearMonth.now(), userCategory.getId());

	@Test
	void 예산_생성_성공() {
		given(userCategoryService.findById(any())).willReturn(userCategory);
		given(userService.findById(any())).willReturn(user);
		given(budgetRepository.findByUserCategoryIdAndRegisterDate(
			any(), any())).willReturn(Optional.empty());

		budgetTotalService.createOrUpdateBudget(user.getId(), createOrUpdateBudgetRequest);
		assertAll(
			() -> verify(userCategoryService).findById(any()),
			() -> verify(userService).findById(any()),
			() -> verify(budgetRepository).findByUserCategoryIdAndRegisterDate(any(), any()),
			() -> verify(budgetRepository).save(any())
		);
	}

	@Test
	void 예산_생성_업데이트처리되는경우() {
		Budget mockBudget = mock(Budget.class);
		given(userCategoryService.findById(any())).willReturn(userCategory);
		given(userService.findById(any())).willReturn(user);
		given(budgetRepository.findByUserCategoryIdAndRegisterDate(
			any(), any())).willReturn(Optional.of(mockBudget));

		budgetTotalService.createOrUpdateBudget(1L, createOrUpdateBudgetRequest);
		assertAll(
			() -> verify(userCategoryService).findById(any()),
			() -> verify(userService).findById(any()),
			() -> verify(budgetRepository).findByUserCategoryIdAndRegisterDate(any(), any()),
			() -> verify(budgetRepository, never()).save(any())
		);
	}

	@Test
	void 예산_생성실패_유저카테고리_없는경우() {
		given(userCategoryService.findById(any())).willThrow(
			new NoSuchElementException(USER_CATEGORY_NOT_FOUND.getMessage()));
		assertThatThrownBy(() -> budgetTotalService.createOrUpdateBudget(user.getId(), createOrUpdateBudgetRequest))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage(USER_CATEGORY_NOT_FOUND.getMessage());
	}

}
