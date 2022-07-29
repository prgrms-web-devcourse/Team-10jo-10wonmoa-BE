package com.prgrms.tenwonmoa.domain.accountbook.service;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
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
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.service.UserCategoryService;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.service.UserService;
import com.prgrms.tenwonmoa.exception.message.Message;

@DisplayName("가계부 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class IncomeTotalServiceTest {

	@Mock
	private UserService userService;
	@Mock
	private UserCategoryService userCategoryService;
	@Mock
	private IncomeService incomeService;
	@InjectMocks
	private IncomeTotalService accountBookService;

	private final Income income = createIncome(createUserCategory(createUser(), createIncomeCategory()));
	private final UserCategory userCategory = income.getUserCategory();
	private final User user = income.getUser();

	private final CreateIncomeRequest request = new CreateIncomeRequest(LocalDateTime.now(),
		1000L,
		"content",
		1L);

	@Test
	void 수입_생성_성공() {
		given(userService.findById(any())).willReturn(user);
		given(userCategoryService.getById(any())).willReturn(userCategory);
		given(incomeService.save(income)).willReturn(1L);

		Long savedId = accountBookService.createIncome(user.getId(), request);
		assertAll(
			() -> assertThat(savedId).isEqualTo(1L),
			() -> verify(incomeService).save(income)
		);
	}

	@Test
	void 수입_생성실패_유저정보_없는경우() {
		given(userService.findById(any())).willThrow(new NoSuchElementException(Message.USER_NOT_FOUND.getMessage()));
		assertThatThrownBy(() -> accountBookService.createIncome(user.getId(), request))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage(Message.USER_NOT_FOUND.getMessage());
	}

	@Test
	void 수입_생성실패_유저카테고리_없는경우() {
		given(userService.findById(any())).willReturn(user);
		given(userCategoryService.getById(any())).willThrow(
			new NoSuchElementException(Message.USER_CATEGORY_NOT_FOUND.getMessage()));
		assertThatThrownBy(() -> accountBookService.createIncome(user.getId(), request))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage(Message.USER_CATEGORY_NOT_FOUND.getMessage());
	}
}
