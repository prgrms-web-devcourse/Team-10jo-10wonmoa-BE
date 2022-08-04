package com.prgrms.tenwonmoa.domain.accountbook.repository;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.prgrms.tenwonmoa.common.RepositoryTest;
import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindDayAccountResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindSumResponse;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomImpl;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;
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

	private Category expenditureCategory;

	private Category incomeCategory;

	private UserCategory userExpenditureCategory;

	private UserCategory userIncomeCategory;

	@BeforeEach
	void setup() {
		user = save(createUser());
		expenditureCategory = save(createExpenditureCategory());
		incomeCategory = save(createIncomeCategory());
		userExpenditureCategory = save(new UserCategory(user, expenditureCategory));
		userIncomeCategory = save(new UserCategory(user, incomeCategory));
	}

	@Nested
	@DisplayName("월별 수입과 지출의 합계를 조회 중")
	class FindMonthSumQuery {

		@Test
		public void 수입과_지출_모두_존재할경우() {
			createExpenditures(10);
			createIncomes(10);

			FindSumResponse monthSum = accountBookQueryRepository.findMonthSum(user.getId(),
				LocalDate.parse("2022-08-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")));

			assertThat(monthSum.getIncomeSum()).isEqualTo(10000L);
			assertThat(monthSum.getExpenditureSum()).isEqualTo(10000L);
			assertThat(monthSum.getTotalSum()).isEqualTo(0);
		}

		@Test
		public void 수입만_존재할경우() {
			createIncomes(7);

			FindSumResponse monthSum = accountBookQueryRepository.findMonthSum(user.getId(),
				LocalDate.parse("2022-08-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")));

			assertThat(monthSum.getIncomeSum()).isEqualTo(7000L);
			assertThat(monthSum.getExpenditureSum()).isEqualTo(0L);
			assertThat(monthSum.getTotalSum()).isEqualTo(7000L);
		}

		@Test
		public void 지출만_존재할경우() {
			createExpenditures(8);

			FindSumResponse monthSum = accountBookQueryRepository.findMonthSum(user.getId(),
				LocalDate.parse("2022-08-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")));

			assertThat(monthSum.getIncomeSum()).isEqualTo(0L);
			assertThat(monthSum.getExpenditureSum()).isEqualTo(8000L);
			assertThat(monthSum.getTotalSum()).isEqualTo(-8000L);
		}

		@Test
		public void 월간_수입과_지출이_없을경우() {
			FindSumResponse monthSum = accountBookQueryRepository.findMonthSum(user.getId(),
				LocalDate.parse("2022-08-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")));

			assertThat(monthSum.getIncomeSum()).isEqualTo(0L);
			assertThat(monthSum.getExpenditureSum()).isEqualTo(0L);
			assertThat(monthSum.getTotalSum()).isEqualTo(0L);
		}

	}

	@Nested
	@DisplayName("연간 수입과 지출의 합계를 조회")
	class FindYearSumQuery {
		@Test
		public void 수입과_지출_모두_존재할경우() {
			createExpenditures(10);
			createIncomes(10);

			FindSumResponse monthSum = accountBookQueryRepository.findYearSum(user.getId(), 2022);

			assertThat(monthSum.getIncomeSum()).isEqualTo(10000L);
			assertThat(monthSum.getExpenditureSum()).isEqualTo(10000L);
			assertThat(monthSum.getTotalSum()).isEqualTo(0);
		}

		@Test
		public void 수입만_존재할경우() {
			createIncomes(7);

			FindSumResponse yearSum = accountBookQueryRepository.findYearSum(user.getId(), 2022);

			assertThat(yearSum.getIncomeSum()).isEqualTo(7000L);
			assertThat(yearSum.getExpenditureSum()).isEqualTo(0L);
			assertThat(yearSum.getTotalSum()).isEqualTo(7000L);
		}

		@Test
		public void 지출만_존재할경우() {
			createExpenditures(8);

			FindSumResponse yearSum = accountBookQueryRepository.findYearSum(user.getId(), 2022);

			assertThat(yearSum.getIncomeSum()).isEqualTo(0L);
			assertThat(yearSum.getExpenditureSum()).isEqualTo(8000L);
			assertThat(yearSum.getTotalSum()).isEqualTo(-8000L);
		}

		@Test
		public void 연간_수입과_지출이_없을경우() {
			FindSumResponse yearSum = accountBookQueryRepository.findYearSum(user.getId(), 2022);

			assertThat(yearSum.getIncomeSum()).isEqualTo(0L);
			assertThat(yearSum.getExpenditureSum()).isEqualTo(0L);
			assertThat(yearSum.getTotalSum()).isEqualTo(0L);
		}
	}

	@Nested
	@DisplayName("일별 상세내역 페이징 조회 중")
	class FindDayAccount {

		@Test
		public void 해당_페이징에_데이터가_없을때() {

			PageCustomImpl<FindDayAccountResponse> dailyAccount = accountBookQueryRepository.findDailyAccount(
				user.getId(),
				new PageCustomRequest(1, 10),
				LocalDate.parse("2022-08-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
			);

			List<FindDayAccountResponse> results = dailyAccount.getResults();

			assertThat(results.size()).isEqualTo(0);
			assertThat(dailyAccount.getCurrentPage()).isEqualTo(1);
			assertThat(dailyAccount.getNextPage()).isNull();
		}

		@Test
		public void 날짜를_수입과_집합의_날짜_합집합을_10개_반환하여_페이징처리한다() {
			createExpenditures(10);
			createIncomes(10);

			PageCustomImpl<FindDayAccountResponse> dailyAccount = accountBookQueryRepository.findDailyAccount(
				user.getId(),
				new PageCustomRequest(1, 10),
				LocalDate.parse("2022-08-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
			);

			List<FindDayAccountResponse> results = dailyAccount.getResults();

			// 페이징 잘 처리 되었는지
			assertThat(dailyAccount.getCurrentPage()).isEqualTo(1);
			assertThat(dailyAccount.getNextPage()).isEqualTo(2);
			assertThat(results.size()).isEqualTo(10);

			//날짜 모두 정렬 되었는지
			for (int i = 0; i < results.size(); i++) {
				FindDayAccountResponse dayAccountResponse = results.get(i);
				assertThat(dayAccountResponse.getRegisterDate().getDayOfMonth()).isEqualTo(29 - i * 2);
			}

		}

		@Test
		public void 마지막_페이지일_경우_응답에서_nextPage를_null로_응답한다() {
			createExpenditures(10);
			createIncomes(10);

			PageCustomImpl<FindDayAccountResponse> dailyAccount = accountBookQueryRepository.findDailyAccount(
				user.getId(),
				new PageCustomRequest(2, 10),
				LocalDate.parse("2022-08-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
			);

			List<FindDayAccountResponse> results = dailyAccount.getResults();

			// 페이징 잘 처리 되었는지
			assertThat(dailyAccount.getCurrentPage()).isEqualTo(2);
			assertThat(dailyAccount.getNextPage()).isNull();
			assertThat(results.size()).isEqualTo(5);
		}
	}

	private void createExpenditures(int count) {
		for (int i = 0; i < count; i++) {
			expenditureRepository.save(new Expenditure(
				LocalDateTime.of(2022, 8, 1 + i * 2, 0, 0),
				1000L,
				"지출" + i,
				expenditureCategory.getName(),
				user,
				userExpenditureCategory
			));
		}
	}

	private void createIncomes(int count) {
		for (int i = 0; i < count; i++) {
			incomeRepository.save(new Income(
				LocalDateTime.of(2022, 8, 11 + i * 2, 0, 0),
				1000L,
				"수입" + i,
				incomeCategory.getName(),
				user,
				userIncomeCategory
			));
		}
	}
}

