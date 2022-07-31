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

import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.dto.CreateIncomeRequest;
import com.prgrms.tenwonmoa.domain.accountbook.dto.UpdateIncomeRequest;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.service.UserCategoryService;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.service.UserService;

@DisplayName("가계부 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class IncomeTotalServiceTest {
	@Mock
	private UserCategoryService userCategoryService;
	@Mock
	private IncomeService incomeService;
	@InjectMocks
	private IncomeTotalService incomeTotalService;

	private final Income income = createIncome(createUserCategory(createUser(), createIncomeCategory()));
	private final UserCategory userCategory = income.getUserCategory();
	private final User user = income.getUser();
	private final User mockUser = mock(User.class);

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
		given(incomeService.save(income)).willReturn(1L);

		Long savedId = incomeTotalService.createIncome(user, request);
		assertAll(
			() -> assertThat(savedId).isEqualTo(1L),
			() -> verify(incomeService).save(income)
		);
	}

	@Test
	void 수입_생성실패_유저카테고리_없는경우() {
		given(userCategoryService.findById(any())).willThrow(
			new NoSuchElementException(USER_CATEGORY_NOT_FOUND.getMessage()));
		assertThatThrownBy(() -> incomeTotalService.createIncome(user, request))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage(USER_CATEGORY_NOT_FOUND.getMessage());
	}

	@Test
	void 수입_수정_성공() {
		given(incomeService.findById(any())).willReturn(income);
		given(userCategoryService.findById(any(Long.class))).willReturn(userCategory);
		incomeTotalService.updateIncome(mockUser,
			income.getId(),
			updateIncomeRequest
		);

		assertAll(
			() -> verify(incomeService).findById(any()),
			() -> verify(userCategoryService).findById(any())
		);
	}

	@Test
	void 수입_수정_실패_수입정보_없는경우() {
		given(incomeService.findById(any())).willThrow(
			new NoSuchElementException(INCOME_NOT_FOUND.getMessage()));
		assertThatThrownBy(() -> incomeTotalService.updateIncome(user,
			income.getId(),
			updateIncomeRequest))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage(INCOME_NOT_FOUND.getMessage());
	}

	@Test
	void 수입_수정_실패_유저카테고리_없는경우() {
		given(incomeService.findById(any())).willReturn(income);
		given(userCategoryService.findById(any())).willThrow(
			new NoSuchElementException(USER_CATEGORY_NOT_FOUND.getMessage()));
		assertThatThrownBy(() -> incomeTotalService.updateIncome(mockUser,
			income.getId(),
			updateIncomeRequest))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage(USER_CATEGORY_NOT_FOUND.getMessage());
	}
}
