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
import com.prgrms.tenwonmoa.domain.accountbook.dto.CalendarCondition;
import com.prgrms.tenwonmoa.domain.accountbook.dto.DateDetail;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindCalendarResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindDayAccountResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindMonthAccountResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindSumResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.MonthCondition;
import com.prgrms.tenwonmoa.domain.accountbook.dto.MonthDetail;
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
			createExpenditures(10, 2022, 8);
			createIncomes(10, 2022, 8);

			FindSumResponse monthSum = accountBookQueryRepository.findMonthSum(user.getId(),
				LocalDate.parse("2022-08-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")));

			assertThat(monthSum.getIncomeSum()).isEqualTo(10000L);
			assertThat(monthSum.getExpenditureSum()).isEqualTo(10000L);
			assertThat(monthSum.getTotalSum()).isEqualTo(0);
		}

		@Test
		public void 수입만_존재할경우() {
			createIncomes(7, 2022, 8);

			FindSumResponse monthSum = accountBookQueryRepository.findMonthSum(user.getId(),
				LocalDate.parse("2022-08-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")));

			assertThat(monthSum.getIncomeSum()).isEqualTo(7000L);
			assertThat(monthSum.getExpenditureSum()).isEqualTo(0L);
			assertThat(monthSum.getTotalSum()).isEqualTo(7000L);
		}

		@Test
		public void 지출만_존재할경우() {
			createExpenditures(8, 2022, 8);

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
			createExpenditures(10, 2022, 8);
			createIncomes(10, 2022, 8);

			FindSumResponse monthSum = accountBookQueryRepository.findYearSum(user.getId(), 2022);

			assertThat(monthSum.getIncomeSum()).isEqualTo(10000L);
			assertThat(monthSum.getExpenditureSum()).isEqualTo(10000L);
			assertThat(monthSum.getTotalSum()).isEqualTo(0);
		}

		@Test
		public void 수입만_존재할경우() {
			createIncomes(7, 2022, 8);

			FindSumResponse yearSum = accountBookQueryRepository.findYearSum(user.getId(), 2022);

			assertThat(yearSum.getIncomeSum()).isEqualTo(7000L);
			assertThat(yearSum.getExpenditureSum()).isEqualTo(0L);
			assertThat(yearSum.getTotalSum()).isEqualTo(7000L);
		}

		@Test
		public void 지출만_존재할경우() {
			createExpenditures(8, 2022, 8);

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
			createExpenditures(10, 2022, 8);
			createIncomes(10, 2022, 8);

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
			createExpenditures(10, 2022, 8);
			createIncomes(10, 2022, 8);

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

	@Nested
	@DisplayName("월별 상세내역 조회")
	class FindMonthAccount {

		LocalDateTime now = LocalDateTime.of(2022, 8, 1, 0, 0);
		int currentYear = 2022;
		int pastYear = 2021;
		int futureYear = 2023;

		@Test
		public void 현재_년도2022에서_현재월8월보다_작성한내역이_과거일때는_8월부터_1월까지_내역을_보여준다() {

			/**
			 * given
			 * 8월 지출: 0, 수입: 0
			 * 7월 지출: 2000, 수입: 0
			 * 6월 지출: 2000, 수입: 2000
			 * 5월 지출: 2000, 수입: 2000
			 * 4월 지출: 0, 수입: 2000
			 * 3월 지출: 0, 수입: 2000
			 * 2월 지출: 0, 수입: 0
			 * 1월 지출: 0, 수입: 0
			 */
			createExpenditures(2, currentYear, 7);
			createExpenditures(2, currentYear, 6);
			createExpenditures(2, currentYear, 5);
			createIncomes(2, currentYear, 6);
			createIncomes(2, currentYear, 5);
			createIncomes(2, currentYear, 4);
			createIncomes(2, currentYear, 3);

			//when
			FindMonthAccountResponse monthAccount = accountBookQueryRepository.findMonthAccount(user.getId(),
				new MonthCondition(now, currentYear));

			List<MonthDetail> results = monthAccount.getResults();

			for (int i = 8; i >= 1; i--) {
				MonthDetail monthDetail = results.get(8 - i);

				if (i == 7) {
					checkMonthDetail(monthDetail, i, 2000L, 0L, -2000L);
				}

				if (i == 6 || i == 5) {
					checkMonthDetail(monthDetail, i, 2000L, 2000L, 0L);
				}

				if (i == 4 && i <= 3) {
					checkMonthDetail(monthDetail, i, 0L, 2000L, 2000L);
				}

				if (i <= 2 || i == 8) {
					checkMonthDetail(monthDetail, i, 0L, 0L, 0L);
				}
			}
		}

		@Test
		public void 현재_년도2022에서_현재월8월보다_작성한내역_미래11월이_있을때는_11월부터_1월까지_내역을_보여준다() {
			/**
			 * given
			 * 11월 지출: 4000, 수입: 3000
			 * 10월 지출: 0, 수입: 0
			 * 9월 지출: 0, 수입: 0
			 * 08월 지출: 2000, 수입: 0
			 * 07월 지출: 2000, 수입: 0
			 * 06월 지출: 2000, 수입: 2000
			 * 05월 지출: 2000, 수입: 2000
			 * 04월 지출: 0, 수입: 2000
			 * 03월 지출: 0, 수입: 2000
			 * 02월 지출: 0, 수입: 0
			 * 01월 지출: 0, 수입: 0
			 */
			createExpenditures(4, currentYear, 11);
			createIncomes(3, currentYear, 11);
			createExpenditures(2, currentYear, 8);
			createExpenditures(2, currentYear, 7);
			createExpenditures(2, currentYear, 6);
			createExpenditures(2, currentYear, 5);
			createIncomes(2, currentYear, 6);
			createIncomes(2, currentYear, 5);
			createIncomes(2, currentYear, 4);
			createIncomes(2, currentYear, 3);

			//when
			FindMonthAccountResponse monthAccount = accountBookQueryRepository.findMonthAccount(user.getId(),
				new MonthCondition(now, currentYear));

			List<MonthDetail> results = monthAccount.getResults();

			for (int i = 11; i >= 1; i--) {
				MonthDetail monthDetail = results.get(11 - i);

				if (i == 11) {
					checkMonthDetail(monthDetail, i, 4000L, 3000L, -1000L);
				}

				if (i == 8 || i == 7) {
					checkMonthDetail(monthDetail, i, 2000L, 0L, -2000L);
				}

				if (i == 6 || i == 5) {
					checkMonthDetail(monthDetail, i, 2000L, 2000L, 0L);
				}

				if (i == 4 && i <= 3) {
					checkMonthDetail(monthDetail, i, 0L, 2000L, 2000L);
				}

				if (i <= 2 || i == 10 || i == 9) {
					checkMonthDetail(monthDetail, i, 0L, 0L, 0L);
				}
			}
		}

		@Test
		public void 현재_년도보다_과거년도를_조회할때는_12월에서_1월까지_모두보여준다() {
			/**
			 *	given
			 * 12~8월, 4~1월은 지출과 수입 모두 0원
			 * 7월 지출: 2000, 수입: 0
			 * 6월 지출: 2000, 수입: 2000
			 * 5월 지출: 2000, 수입: 2000
			 */
			createExpenditures(2, pastYear, 7);
			createExpenditures(2, pastYear, 6);
			createExpenditures(2, pastYear, 5);
			createIncomes(2, pastYear, 6);
			createIncomes(2, pastYear, 5);

			//when
			FindMonthAccountResponse monthAccount = accountBookQueryRepository.findMonthAccount(user.getId(),
				new MonthCondition(now, pastYear));

			List<MonthDetail> results = monthAccount.getResults();

			for (int i = 12; i >= 1; i--) {
				MonthDetail monthDetail = results.get(12 - i);

				if (i == 7) {
					checkMonthDetail(monthDetail, i, 2000L, 0L, -2000L);
					continue;
				}

				if (i == 6 || i == 5) {
					checkMonthDetail(monthDetail, i, 2000L, 2000L, 0L);
					continue;
				}

				checkMonthDetail(monthDetail, i, 0L, 0L, 0L);
			}

		}

		@Test
		public void 현재_년도보다_미래년도를_조회할때는_작성한_월만_보여준다() {
			/**
			 *	given
			 * 7월 지출: 2000, 수입: 0
			 * 6월 지출: 2000, 수입: 2000
			 * 5월 지출: 2000, 수입: 2000
			 */
			createExpenditures(2, futureYear, 7);
			createExpenditures(2, futureYear, 6);
			createExpenditures(2, futureYear, 5);
			createIncomes(2, futureYear, 6);
			createIncomes(2, futureYear, 5);

			//when
			FindMonthAccountResponse monthAccount = accountBookQueryRepository.findMonthAccount(user.getId(),
				new MonthCondition(now, futureYear));

			List<MonthDetail> results = monthAccount.getResults();

			for (int i = 7; i >= 5; i--) {
				MonthDetail monthDetail = results.get(7 - i);

				if (i == 7) {
					checkMonthDetail(monthDetail, i, 2000L, 0L, -2000L);
					continue;
				}

				checkMonthDetail(monthDetail, i, 2000L, 2000L, 0L);
			}
		}
	}

	@Nested
	@DisplayName("달력 상세내역 조회")
	class FindCalendarAccount {

		@Test
		public void 달력에서_2020년_2월은_윤년이다() {
			/**
			 * given 2020년 2월
			 * 1 ~ 19 홀수일은 지출 1000원
			 * 11 ~ 29 홀수일은 수입 1000원
			 * 짝수일은 지출 수입 모두 0원
			 * */
			int year = 2020;
			int month = 2;

			CalendarCondition condition = new CalendarCondition(LocalDate.of(year, month, 1));

			createExpenditures(10, year, month);
			createIncomes(10, year, month);

			FindCalendarResponse calendarAccount = accountBookQueryRepository.findCalendarAccount(user.getId(),
				condition);

			List<DateDetail> results = calendarAccount.getResults();

			// 2020년 2월이 윤년인지
			assertThat(calendarAccount.getMonth()).isEqualTo(2);
			assertThat(results.size()).isEqualTo(29);
			
			for (int i = 0; i < results.size(); i++) {

				DateDetail dateDetail = results.get(i);
				int day = i + 1;

				if (day % 2 == 0) {
					assertThat(dateDetail.getIncomeSum()).isEqualTo(0L);
					assertThat(dateDetail.getExpenditureSum()).isEqualTo(0L);
					assertThat(dateDetail.getTotalSum()).isEqualTo(0L);
					continue;
				}

				if (day % 2 == 1) {

					if (day < 11) {
						assertThat(dateDetail.getIncomeSum()).isEqualTo(0L);
						assertThat(dateDetail.getExpenditureSum()).isEqualTo(1000L);
						assertThat(dateDetail.getTotalSum()).isEqualTo(-1000L);
						continue;
					}

					if (day > 19) {
						assertThat(dateDetail.getIncomeSum()).isEqualTo(1000L);
						assertThat(dateDetail.getExpenditureSum()).isEqualTo(0L);
						assertThat(dateDetail.getTotalSum()).isEqualTo(1000L);
						continue;
					}

					if (day >= 11 && day <= 19) {
						assertThat(dateDetail.getExpenditureSum()).isEqualTo(1000L);
						assertThat(dateDetail.getIncomeSum()).isEqualTo(1000L);
						assertThat(dateDetail.getTotalSum()).isEqualTo(0L);
						continue;
					}
				}
			}
		}

	}

	private void createExpenditures(int count, int year, int month) {
		for (int i = 0; i < count; i++) {
			expenditureRepository.save(new Expenditure(
				LocalDateTime.of(year, month, 1 + i * 2, 0, 0),
				1000L,
				"지출" + i,
				expenditureCategory.getName(),
				user,
				userExpenditureCategory
			));
		}
	}

	private void createIncomes(int count, int year, int month) {
		for (int i = 0; i < count; i++) {
			incomeRepository.save(new Income(
				LocalDateTime.of(year, month, 11 + i * 2, 0, 0),
				1000L,
				"수입" + i,
				incomeCategory.getName(),
				user,
				userIncomeCategory
			));
		}
	}

	private void checkMonthDetail(MonthDetail monthDetail, int targetMonth, Long targetExpenditure, Long targetIncome,
		Long targetTotal) {
		assertThat(monthDetail.getMonth()).isEqualTo(targetMonth);
		assertThat(monthDetail.getIncomeSum()).isEqualTo(targetIncome);
		assertThat(monthDetail.getExpenditureSum()).isEqualTo(targetExpenditure);
		assertThat(monthDetail.getTotalSum()).isEqualTo(targetTotal);
	}

}

