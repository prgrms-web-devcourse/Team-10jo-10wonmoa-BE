package com.prgrms.tenwonmoa.domain.accountbook.service;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static com.prgrms.tenwonmoa.exception.message.Message.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.dto.income.CreateIncomeRequest;
import com.prgrms.tenwonmoa.domain.accountbook.dto.income.UpdateIncomeRequest;
import com.prgrms.tenwonmoa.domain.accountbook.repository.ExpenditureRepository;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.service.UserCategoryService;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.service.UserService;

@DisplayName("수입 통합 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class IncomeTotalServiceTest {
	@Mock
	private UserCategoryService userCategoryService;
	@Mock
	private IncomeService incomeService;
	@Mock
	private UserService userService;
	@Mock
	private ExpenditureRepository expenditureRepository;
	@InjectMocks
	private IncomeTotalService incomeTotalService;

	private final Long userId = 1L;
	private final Income income = createIncome(createUserCategory(createUser(), createIncomeCategory()));
	private final UserCategory userCategory = income.getUserCategory();
	private final User user = income.getUser();
	private final User mockUser = mock(User.class);
	private final Income mockIncome = mock(Income.class);
	private final UserCategory mockUserCategory = mock(UserCategory.class);

	private final CreateIncomeRequest request = new CreateIncomeRequest(LocalDateTime.now(),
		1000L,
		"content",
		1L);

	private final UpdateIncomeRequest updateIncomeRequest = new UpdateIncomeRequest(LocalDateTime.now(),
		2000L,
		"updateContent",
		2L);
	@Test
	void 수입_생성_성공() {
		validateUserCategoryMock();
		given(userService.findById(anyLong())).willReturn(mockUser);
		given(incomeService.save(income)).willReturn(userId);

		incomeTotalService.createIncome(userId, request);
		assertAll(
			() -> verify(userCategoryService).findById(any()),
			() -> verify(userService).findById(any()),
			() -> verify(incomeService).save(income)
		);
	}

	private void validateUserCategoryMock() {
		given(userCategoryService.findById(any())).willReturn(mockUserCategory);
		given(mockUserCategory.getUser()).willReturn(mockUser);
		given(mockUserCategory.getCategory()).willReturn(new Category("income", CategoryType.INCOME));
		doNothing().when(mockUser).validateLoginUser(any());
	}

	@Test
	void 수입_생성실패_유저카테고리_없는경우() {
		given(userCategoryService.findById(anyLong())).willThrow(
			new NoSuchElementException(USER_CATEGORY_NOT_FOUND.getMessage()));
		assertThatThrownBy(() -> incomeTotalService.createIncome(userId, request))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage(USER_CATEGORY_NOT_FOUND.getMessage());
	}

	@Test
	void 수입_수정_성공() {
		given(incomeService.findById(any())).willReturn(mockIncome);
		given(mockIncome.getUser()).willReturn(mockUser);
		validateUserCategoryMock();
		doNothing().when(mockUser).validateLoginUser(anyLong());

		given(userCategoryService.findById(any(Long.class))).willReturn(mockUserCategory);
		incomeTotalService.updateIncome(userId,
			income.getId(),
			updateIncomeRequest
		);
		assertAll(
			() -> verify(incomeService, never()).deleteById(any()),
			() -> verify(expenditureRepository, never()).save(any(Expenditure.class)),
			() -> verify(incomeService).findById(any()),
			() -> verify(userCategoryService).findById(any())
		);
	}

	@Test
	void 수입_수정_실패_수입정보_없는경우() {
		validateUserCategoryMock();
		given(incomeService.findById(any())).willThrow(
			new NoSuchElementException(INCOME_NOT_FOUND.getMessage()));
		assertThatThrownBy(() -> incomeTotalService.updateIncome(userId,
			income.getId(),
			updateIncomeRequest))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage(INCOME_NOT_FOUND.getMessage());
	}

	@Test
	void 수입_수정_실패_유저카테고리_없는경우() {
		given(userCategoryService.findById(any())).willThrow(
			new NoSuchElementException(USER_CATEGORY_NOT_FOUND.getMessage()));
		assertThatThrownBy(() -> incomeTotalService.updateIncome(userId,
			income.getId(),
			updateIncomeRequest))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage(USER_CATEGORY_NOT_FOUND.getMessage());
	}
}
