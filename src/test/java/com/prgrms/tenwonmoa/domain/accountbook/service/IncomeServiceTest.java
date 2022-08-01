package com.prgrms.tenwonmoa.domain.accountbook.service;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
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
import com.prgrms.tenwonmoa.domain.accountbook.dto.income.FindIncomeResponse;
import com.prgrms.tenwonmoa.domain.accountbook.repository.IncomeRepository;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.exception.message.Message;

@DisplayName("수입 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class IncomeServiceTest {
	@Mock
	private IncomeRepository incomeRepository;

	@InjectMocks
	private IncomeService incomeService;

	private final Income income = createIncome(createUserCategory(createUser(), createIncomeCategory()));

	private final Long incomeId = 1L;

	@Test
	void 수입저장_성공() {
		given(incomeRepository.save(income)).willReturn(income);

		Long savedId = incomeService.save(income);
		assertAll(
			() -> assertThat(savedId).isEqualTo(income.getId()),
			() -> verify(incomeRepository).save(income)
		);
	}

	@Test
	void 아이디로_수입조회_성공() {
		User mockUser = mock(User.class);
		doNothing().when(mockUser).validateLogin(income.getUser());
		given(incomeRepository.findById(any(Long.class))).willReturn(Optional.of(income));

		FindIncomeResponse findIncomeResponse = incomeService.findIncome(incomeId, mockUser);
		assertAll(
			() -> assertThat(findIncomeResponse.getId()).isEqualTo(income.getId()),
			() -> assertThat(findIncomeResponse.getRegisterDate()).isEqualTo(income.getRegisterDate()),
			() -> assertThat(findIncomeResponse.getAmount()).isEqualTo(income.getAmount()),
			() -> assertThat(findIncomeResponse.getContent()).isEqualTo(income.getContent()),
			() -> assertThat(findIncomeResponse.getCategoryName()).isEqualTo(income.getCategoryName()),
			() -> verify(incomeRepository).findById(incomeId)
		);
	}

	@Test
	void 아이디로_조회_수입정보가없으면_실패() {
		given(incomeRepository.findById(any(Long.class))).willThrow(
			new NoSuchElementException(Message.INCOME_NOT_FOUND.getMessage()));

		assertThatThrownBy(() -> incomeRepository.findById(1L))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage(Message.INCOME_NOT_FOUND.getMessage());
	}
}
