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
		given(userCategoryService.findById(any())).willReturn(userCategory);
		given(userService.findById(anyLong())).willReturn(user);
		given(incomeService.save(income)).willReturn(userId);

		Long savedId = incomeTotalService.createIncome(userId, request);
		assertAll(
			() -> assertThat(savedId).isEqualTo(userId),
			() -> verify(incomeService).save(income)
		);
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
		doNothing().when(mockUser).validateLoginUser(anyLong());

		given(userCategoryService.findById(any(Long.class))).willReturn(userCategory);
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
	void 수입_수정_지출로_변경되는경우_성공() {
		given(incomeService.findById(any())).willReturn(mockIncome);
		given(mockIncome.getUser()).willReturn(mockUser);
		doNothing().when(mockUser).validateLoginUser(anyLong());
		UserCategory expenditureCategory = createUserCategory(income.getUser(), createExpenditureCategory());
		given(userCategoryService.findById(anyLong())).willReturn(expenditureCategory);

		// when
		incomeTotalService.updateIncome(anyLong(),
			income.getId(),
			updateIncomeRequest
		);

		// then
		assertAll(
			() -> verify(incomeService).deleteById(any()),
			() -> verify(expenditureRepository).save(any(Expenditure.class)),
			() -> verify(mockIncome, never()).update(any(UserCategory.class), any(UpdateIncomeRequest.class))
		);
	}

	@Test
	void 수입_수정_실패_수입정보_없는경우() {
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
		given(incomeService.findById(any())).willReturn(mockIncome);
		given(mockIncome.getUser()).willReturn(mockUser);
		doNothing().when(mockUser).validateLoginUser(anyLong());

		given(userCategoryService.findById(any())).willThrow(
			new NoSuchElementException(USER_CATEGORY_NOT_FOUND.getMessage()));
		assertThatThrownBy(() -> incomeTotalService.updateIncome(userId,
			income.getId(),
			updateIncomeRequest))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage(USER_CATEGORY_NOT_FOUND.getMessage());
	}
}
