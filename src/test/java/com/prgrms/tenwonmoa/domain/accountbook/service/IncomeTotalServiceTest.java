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
	private static final Long USER_ID = 1L;
	private static final Long INCOME_ID = 1L;
	private static final Expenditure expenditure = createExpenditure(createUserCategory(createUser(),
		createExpenditureCategory()));
	private static final User mockUser = mock(User.class);
	private static final Income mockIncome = mock(Income.class);
	private static final UserCategory mockUserCategory = mock(UserCategory.class);
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
		validateUserCategoryMock(new Category("income", CategoryType.INCOME));
		given(userService.findById(anyLong())).willReturn(mockUser);
		given(incomeService.save(any(Income.class))).willReturn(USER_ID);

		incomeTotalService.createIncome(USER_ID, request);
		assertAll(
			() -> verify(userCategoryService).findById(anyLong()),
			() -> verify(userService).findById(anyLong()),
			() -> verify(incomeService).save(any(Income.class))
		);
	}

	@Test
	void 수입_생성실패_유저카테고리_없는경우() {
		given(userCategoryService.findById(anyLong())).willThrow(
			new NoSuchElementException(USER_CATEGORY_NOT_FOUND.getMessage()));

		assertThatThrownBy(() -> incomeTotalService.createIncome(USER_ID, request))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage(USER_CATEGORY_NOT_FOUND.getMessage());
	}

	@Test
	void 수입_수정_성공() {
		given(incomeService.findById(anyLong())).willReturn(mockIncome);
		given(mockIncome.getUser()).willReturn(mockUser);
		validateUserCategoryMock(new Category("income", CategoryType.INCOME));
		given(userCategoryService.findById(anyLong())).willReturn(mockUserCategory);

		incomeTotalService.updateIncome(USER_ID, INCOME_ID, updateIncomeRequest);
		assertAll(
			() -> verify(incomeService, never()).deleteById(anyLong()),
			() -> verify(expenditureRepository, never()).save(any(Expenditure.class)),
			() -> verify(incomeService).findById(anyLong()),
			() -> verify(userCategoryService).findById(anyLong())
		);
	}

	@Test
	void 수입_지출변경_성공() {
		given(userCategoryService.findById(anyLong())).willReturn(mockUserCategory);
		validateUserCategoryMock(expenditure.getUserCategory().getCategory());
		given(mockUserCategory.getCategoryName()).willReturn("categoryName");
		given(expenditureRepository.save(any(Expenditure.class))).willReturn(expenditure);
		doNothing().when(incomeService).deleteById(anyLong());

		incomeTotalService.updateIncome(USER_ID, INCOME_ID, updateIncomeRequest);
		assertAll(
			() -> verify(incomeService).deleteById(anyLong()),
			() -> verify(expenditureRepository).save(any(Expenditure.class)),
			() -> verify(incomeService, never()).findById(any())
		);
	}

	@Test
	void 수입_수정_실패_수입정보_없는경우() {
		validateUserCategoryMock(new Category("income", CategoryType.INCOME));

		given(incomeService.findById(anyLong())).willThrow(
			new NoSuchElementException(INCOME_NOT_FOUND.getMessage()));
		assertThatThrownBy(() -> incomeTotalService.updateIncome(USER_ID, INCOME_ID, updateIncomeRequest))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage(INCOME_NOT_FOUND.getMessage());
	}

	@Test
	void 수입_수정_실패_유저카테고리_없는경우() {
		given(userCategoryService.findById(anyLong())).willThrow(
			new NoSuchElementException(USER_CATEGORY_NOT_FOUND.getMessage()));
		assertThatThrownBy(() -> incomeTotalService.updateIncome(USER_ID, INCOME_ID, updateIncomeRequest))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage(USER_CATEGORY_NOT_FOUND.getMessage());
	}

	private void validateUserCategoryMock(Category category) {
		given(userCategoryService.findById(anyLong())).willReturn(mockUserCategory);
		given(mockUserCategory.getUser()).willReturn(mockUser);
		given(mockUserCategory.getCategory()).willReturn(category);
		doNothing().when(mockUser).validateLoginUser(anyLong());
	}
}
