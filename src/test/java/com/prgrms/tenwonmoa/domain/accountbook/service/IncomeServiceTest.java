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
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindIncomeResponse;
import com.prgrms.tenwonmoa.domain.accountbook.repository.IncomeRepository;
import com.prgrms.tenwonmoa.exception.message.Message;

@DisplayName("수입 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class IncomeServiceTest {
	@Mock
	private IncomeRepository incomeRepository;

	@InjectMocks
	private IncomeService incomeService;

	private final Income income = createIncome(createUserCategory(createUser(), createCategory()));

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
		Long incomeId = 1L;
		Long userId = 1L;

		given(incomeRepository.findByIdAndUserId(any(Long.class), any(Long.class))).willReturn(Optional.of(income));

		FindIncomeResponse findIncomeResponse = incomeService.findIncome(incomeId, userId);
		assertAll(
			() -> assertThat(findIncomeResponse.getId()).isEqualTo(income.getId()),
			() -> assertThat(findIncomeResponse.getRegisterDate()).isEqualTo(income.getRegisterDate()),
			() -> assertThat(findIncomeResponse.getAmount()).isEqualTo(income.getAmount()),
			() -> assertThat(findIncomeResponse.getContent()).isEqualTo(income.getContent()),
			() -> assertThat(findIncomeResponse.getCategoryName()).isEqualTo(income.getCategoryName()),
			() -> verify(incomeRepository).findByIdAndUserId(incomeId, userId)
		);
	}

	@Test
	void 아이디로_조회_수입정보가없으면_실패() {
		given(incomeRepository.findByIdAndUserId(any(Long.class), any(Long.class))).willThrow(
			new NoSuchElementException(Message.INCOME_NOT_FOUND.getMessage()));

		assertThatThrownBy(() -> incomeRepository.findByIdAndUserId(1L, 1L))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage(Message.INCOME_NOT_FOUND.getMessage());
	}
}
