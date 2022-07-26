package com.prgrms.tenwonmoa.domain.accountbook.service;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.repository.IncomeRepository;

@DisplayName("수입 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class IncomeServiceTest {
	@Mock
	private IncomeRepository incomeRepository;

	@InjectMocks
	private IncomeService incomeService;

	private final Income income = createIncome();

	@Test
	void 수입저장_성공() {
		given(incomeRepository.save(income)).willReturn(income);

		Long savedId = incomeService.save(income);
		assertAll(
			() -> assertThat(savedId).isEqualTo(income.getId()),
			() -> verify(incomeRepository).save(income)
		);
	}
}
