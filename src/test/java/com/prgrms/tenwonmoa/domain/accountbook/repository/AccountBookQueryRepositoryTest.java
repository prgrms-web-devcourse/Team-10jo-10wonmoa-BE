package com.prgrms.tenwonmoa.domain.accountbook.repository;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.prgrms.tenwonmoa.common.RepositoryTest;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindMonthSumResponse;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;

@DisplayName("가계부(AccountBook) 조회 레포지토리 테스트")
class AccountBookQueryRepositoryTest extends RepositoryTest {

	@Autowired
	private AccountBookQueryRepository accountBookQueryRepository;

	@Autowired
	private ExpenditureRepository expenditureRepository;

	@Autowired
	private IncomeRepository incomeRepository;

	private User user;

	private Category category;

	private UserCategory userCategory;

	@BeforeEach
	void setup() {
		user = save(createUser());
		category = save(createExpenditureCategory());
		userCategory = save(new UserCategory(user, category));
	}

	@Nested
	@DisplayName("월별 수입과 지출의 합계를 조회 중")
	class FindMonthSumQuery {

		@Test
		public void 수입과_지출_모두_존재할경우() {
			createExpenditures(10);
			createIncomes(10);

			FindMonthSumResponse monthSum = accountBookQueryRepository.findMonthSum(user.getId(),
				LocalDate.parse("2022-07-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")));

			Assertions.assertThat(monthSum.getMonthIncome()).isEqualTo(10000L);
			Assertions.assertThat(monthSum.getMonthExpenditure()).isEqualTo(10000L);
			Assertions.assertThat(monthSum.getMonthTotal()).isEqualTo(0);
		}

		@Test
		public void 수입만_존재할경우() {
			createIncomes(7);

			FindMonthSumResponse monthSum = accountBookQueryRepository.findMonthSum(user.getId(),
				LocalDate.parse("2022-07-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")));

			Assertions.assertThat(monthSum.getMonthIncome()).isEqualTo(7000L);
			Assertions.assertThat(monthSum.getMonthExpenditure()).isEqualTo(0L);
			Assertions.assertThat(monthSum.getMonthTotal()).isEqualTo(7000L);
		}

		@Test
		public void 지출만_존재할경우() {
			createExpenditures(8);

			FindMonthSumResponse monthSum = accountBookQueryRepository.findMonthSum(user.getId(),
				LocalDate.parse("2022-07-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")));

			Assertions.assertThat(monthSum.getMonthIncome()).isEqualTo(0L);
			Assertions.assertThat(monthSum.getMonthExpenditure()).isEqualTo(8000L);
			Assertions.assertThat(monthSum.getMonthTotal()).isEqualTo(-8000L);
		}

		@Test
		public void 월간_수입과_지출이_없을경우() {
			FindMonthSumResponse monthSum = accountBookQueryRepository.findMonthSum(user.getId(),
				LocalDate.parse("2022-07-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")));

			Assertions.assertThat(monthSum.getMonthIncome()).isEqualTo(0L);
			Assertions.assertThat(monthSum.getMonthExpenditure()).isEqualTo(0L);
			Assertions.assertThat(monthSum.getMonthTotal()).isEqualTo(0L);
		}

	}

	private void createExpenditures(int count) {
		for (int i = 0; i < count; i++) {
			expenditureRepository.save(createExpenditure(userCategory));
		}
	}

	private void createIncomes(int count) {
		for (int i = 0; i < count; i++) {
			incomeRepository.save(createIncome(userCategory));
		}
	}
}

