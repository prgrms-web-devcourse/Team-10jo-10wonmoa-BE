package com.prgrms.tenwonmoa.domain.accountbook.controller.intergration;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import com.prgrms.tenwonmoa.common.BaseControllerIntegrationTest;
import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.dto.DateDetail;
import com.prgrms.tenwonmoa.domain.accountbook.repository.ExpenditureRepository;
import com.prgrms.tenwonmoa.domain.accountbook.repository.IncomeRepository;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.repository.CategoryRepository;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;

@DisplayName("AccountBookQuery 통합 테스트")
public class AccountBookIntergrationTest extends BaseControllerIntegrationTest {

	private static final String BASE_URL = "/api/v1/account-book";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserCategoryRepository userCategoryRepository;

	@Autowired
	private IncomeRepository incomeRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ExpenditureRepository expenditureRepository;

	private User loginUser;

	private Category incomeCategory;

	private Category expenditureCategory;

	private UserCategory incomeUserCategory;

	private UserCategory expenditureUserCategory;

	@BeforeEach
	void setup() throws Exception {
		registerUserAndLogin();
		loginUser = userRepository.findByEmail("testuser@gmail.com").get();

		incomeCategory = categoryRepository.save(createIncomeCategory());
		incomeUserCategory = userCategoryRepository.save(createUserCategory(loginUser, incomeCategory));

		expenditureCategory = categoryRepository.save(createExpenditureCategory());
		expenditureUserCategory = userCategoryRepository.save(createUserCategory(loginUser, expenditureCategory));

		createExpenditures(10, 2022, 8);
		createIncomes(10, 2022, 8);
	}

	@AfterEach
	void teardown() {
		incomeRepository.deleteAllInBatch();
		expenditureRepository.deleteAllInBatch();
		userCategoryRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
		categoryRepository.deleteAllInBatch();
	}

	@Nested
	@DisplayName("일일상세내역 조회 중: ")
	class DescribeOfDailyAccountGet {

		@Test
		public void 성공적으로_조회할_수_있다_200() throws Exception {
			mvc.perform(get(BASE_URL + "/daily")
					.header(HttpHeaders.AUTHORIZATION, accessToken)
					.param("year", "2022")
					.param("month", "8")
					.param("page", "1")
					.param("size", "10")
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("currentPage").value(1))
				.andExpect(jsonPath("totalElements").value(20))
				.andExpect(jsonPath("nextPage").value(2))
				.andExpect(jsonPath("totalPages").value(2));
		}
	}

	@Nested
	@DisplayName("월별합계 조회 중")
	class DescribeOfMonthSumGet {
		@Test
		public void 성공적으로_조회할_수_있다_200() throws Exception {
			mvc.perform(get(BASE_URL + "/sum/month/{date}", "2022-08-01")
					.header(HttpHeaders.AUTHORIZATION, accessToken)
				)
				.andExpect(status().isOk())
				.andExpect(status().isOk())
				.andExpect(jsonPath("incomeSum").value(10000))
				.andExpect(jsonPath("expenditureSum").value(10000))
				.andExpect(jsonPath("totalSum").value(0));

		}
	}

	@Nested
	@DisplayName("연간 합계 조회 중")
	class DescribeOfYearSum {

		private final int year = 2022;

		@Test
		public void 성공적으로_조회할_수_있다_200() throws Exception {
			mvc.perform(get(BASE_URL + "/sum/year/{year}", year)
					.header(HttpHeaders.AUTHORIZATION, accessToken)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("incomeSum").value(10000))
				.andExpect(jsonPath("expenditureSum").value(10000))
				.andExpect(jsonPath("totalSum").value(0));
		}
	}

	@Nested
	@DisplayName("월별 상세 내역 조회 중")
	class DescribeOfMonthAccount {

		private final int year = 2022;

		@Test
		public void 성공적으로_조회할_수_있다_200() throws Exception {
			mvc.perform(get(BASE_URL + "/month/{year}", year)
					.header(HttpHeaders.AUTHORIZATION, accessToken)
				)
				.andExpect(status().isOk());
				// .andExpect(jsonPath("$.results", hasSize(8)));
		}

	}

	@Nested
	@DisplayName("달력 조회 중")
	class DescribeOfCalendar {

		private final int year = 2022;
		private final int month = 8;

		private List<DateDetail> results = new ArrayList<>();

		@Test
		public void 성공적으로_조회할_수_있다_200() throws Exception {
			mvc.perform(get(BASE_URL + "/calendar")
					.header(HttpHeaders.AUTHORIZATION, accessToken)
					.param("year", "2022")
					.param("month", "8")
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.results", hasSize(31)));
		}
	}

	private void createExpenditures(int count, int year, int month) {
		for (int i = 0; i < count; i++) {
			expenditureRepository.save(new Expenditure(
				LocalDateTime.of(year, month, 1 + i * 2, 0, 0),
				1000L,
				"지출" + i,
				expenditureCategory.getName(),
				loginUser,
				expenditureUserCategory
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
				loginUser,
				incomeUserCategory
			));
		}
	}

}
