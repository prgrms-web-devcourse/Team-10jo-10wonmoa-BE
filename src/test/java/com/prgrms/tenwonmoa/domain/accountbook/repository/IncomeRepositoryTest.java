package com.prgrms.tenwonmoa.domain.accountbook.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.prgrms.tenwonmoa.common.fixture.RepositoryFixture;
import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.user.User;

@DisplayName("수입 Repository 테스트")
class IncomeRepositoryTest extends RepositoryFixture {

	@Autowired
	private IncomeRepository incomeRepository;

	@Test
	void 유저아이디와_수입아이디로_조회_성공() {
		// given
		Income income = saveIncome();
		User user = income.getUser();

		// when
		Optional<Income> findIncome = incomeRepository.findByIdAndUserId(income.getId(), user.getId());

		// then
		assertThat(findIncome.isPresent());
		Income getIncome = findIncome.get();
		assertAll(
			() -> assertThat(getIncome.getId()).isEqualTo(income.getId()),
			() -> assertThat(getIncome.getUser().getId()).isEqualTo(user.getId())
		);
	}

	@Test
	void 로그인한아이디가_다른계정의_수입을_조회할수없다() {
		// given
		Income loginIncome = saveIncome();
		Income otherIncome = saveIncome();

		User loginUser = loginIncome.getUser();
		// when
		Optional<Income> findIncome = incomeRepository.findByIdAndUserId(otherIncome.getId(), loginUser.getId());

		// then
		assertThat(findIncome).isEmpty();
	}
}
