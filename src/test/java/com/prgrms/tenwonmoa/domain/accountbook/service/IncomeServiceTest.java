package com.prgrms.tenwonmoa.domain.accountbook.service;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.repository.IncomeRepository;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.exception.message.Message;

@DisplayName("수입 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class IncomeServiceTest {
	public static final Long AUTH_ID = 1L;
	public static final Long INCOME_ID = 1L;
	private static final Income income = createIncome(createUserCategory(createUser(), createIncomeCategory()));
	private static final Income mockIncome = mock(Income.class);
	private static final UserCategory mockUserCategory = mock(UserCategory.class);

	@Mock
	private IncomeRepository incomeRepository;

	@InjectMocks
	private IncomeService incomeService;

	@Test
	void 수입저장_성공() {
		given(incomeRepository.save(income)).willReturn(income);
		// when
		incomeService.save(income);
		// then
		verify(incomeRepository).save(income);
	}

	@Test
	void 아이디로_수입조회_성공() {
		given(incomeRepository.findById(anyLong())).willReturn(Optional.of(mockIncome));
		given(mockIncome.getUserCategory()).willReturn(mockUserCategory);

		incomeService.findIncome(AUTH_ID, INCOME_ID);
		verify(incomeRepository).findById(anyLong());
	}

	@Test
	void 아이디로_조회_수입정보가없으면_실패() {
		given(incomeRepository.findById(anyLong())).willThrow(
			new NoSuchElementException(Message.INCOME_NOT_FOUND.getMessage()));

		assertThatThrownBy(() -> incomeRepository.findById(INCOME_ID))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage(Message.INCOME_NOT_FOUND.getMessage());
	}
}
