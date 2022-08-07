package com.prgrms.tenwonmoa.domain.accountbook.controller;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static java.time.LocalDateTime.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.tenwonmoa.common.BaseControllerIntegrationTest;
import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.repository.ExpenditureRepository;
import com.prgrms.tenwonmoa.domain.accountbook.repository.IncomeRepository;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.repository.CategoryRepository;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;

@DisplayName("통계 컨트롤러 통합 테스트")
class StatisticsIntegrationTest extends BaseControllerIntegrationTest {
	private static final String LOCATION_PREFIX = "/api/v1/statistics";
	private static final String YEAR = "year";
	private static final String MONTH = "month";
	private static final String YEAR_MIN_EXP_MSG = "연도의 최솟값은 1900입니다.";
	private static final String MONTH_EXP_MSG = "월은 1~12 범위만 입력할 수 있습니다.";
	private static final String MUST_NOT_BE_NULL = "must not be null";
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserCategoryRepository userCategoryRepository;
	@Autowired
	private IncomeRepository incomeRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ExpenditureRepository expenditureRepository;

	private User loginUser;
	private Category category;
	private UserCategory userCategory;

	private Income saveIncome(UserCategory userCategory, Long amount, LocalDateTime registerDate) {
		return incomeRepository.saveAndFlush(
			new Income(registerDate, amount, "content", userCategory.getCategoryName(), userCategory.getUser(),
				userCategory));
	}

	public Expenditure saveExpenditure(UserCategory userCategory, Long amount, LocalDateTime registerDate) {
		return expenditureRepository.saveAndFlush(
			new Expenditure(registerDate, amount, "content", userCategory.getCategoryName(), userCategory.getUser(),
				userCategory));
	}

	@BeforeEach
	void init() throws Exception {
		registerUserAndLogin();
		loginUser = userRepository.findByEmail("testuser@gmail.com").get();
		category = categoryRepository.save(createIncomeCategory());
		userCategory = userCategoryRepository.save(createUserCategory(loginUser, category));
		initData();
	}

	@AfterEach
	void clear() {
		incomeRepository.deleteAllInBatch();
		expenditureRepository.deleteAllInBatch();
		userCategoryRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
		categoryRepository.deleteAllInBatch();
	}

	@Test
	void 통계_년별조회_성공() throws Exception {
		mvc.perform(get(LOCATION_PREFIX)
				.param(YEAR, "2021")
				.header(HttpHeaders.AUTHORIZATION, accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("year").value(2021))
			.andExpect(jsonPath("month").isEmpty())
			.andExpect(jsonPath("incomeTotalSum").value(1000))
			.andExpect(jsonPath("expenditureTotalSum").value(400))
			.andExpect(jsonPath("incomes").exists())
			.andExpect(jsonPath("expenditures").exists());
	}

	@Test
	void 통계_월별조회_성공() throws Exception {
		mvc.perform(
				get(LOCATION_PREFIX)
					.param(YEAR, "2021")
					.param(MONTH, "7")
					.header(HttpHeaders.AUTHORIZATION, accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("year").value(2021))
			.andExpect(jsonPath("month").value(7))
			.andExpect(jsonPath("incomeTotalSum").value(100))
			.andExpect(jsonPath("expenditureTotalSum").value(200))
			.andExpect(jsonPath("incomes").exists())
			.andExpect(jsonPath("expenditures").exists());
	}

	@Test
	void 통계_월앞에0붙였을때_성공() throws Exception {
		mvc.perform(get(LOCATION_PREFIX)
				.param(YEAR, "2021")
				.param(MONTH, "07")
				.header(HttpHeaders.AUTHORIZATION, accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("year").value(2021))
			.andExpect(jsonPath("month").value(07))
			.andExpect(jsonPath("incomeTotalSum").value(100))
			.andExpect(jsonPath("expenditureTotalSum").value(200))
			.andExpect(jsonPath("incomes").exists())
			.andExpect(jsonPath("expenditures").exists());
	}

	@Test
	void 통계_Valid_월만입력된경우() throws Exception {
		validateSearchStatistics(null, "10", MUST_NOT_BE_NULL);
	}

	@Test
	void 통계_Valid_년도는_1900이상만_가능() throws Exception {
		validateSearchStatistics("1899", "10", YEAR_MIN_EXP_MSG);
		validateSearchStatistics("0", "10", YEAR_MIN_EXP_MSG);
		validateSearchStatistics("-1", "10", YEAR_MIN_EXP_MSG);
	}

	@Test
	void 통계_Valid_잘못된_월_입력() throws Exception {
		validateSearchStatistics("2020", "-1", MONTH_EXP_MSG);
		validateSearchStatistics("2020", "0", MONTH_EXP_MSG);
		validateSearchStatistics("2020", "13", MONTH_EXP_MSG);
	}

	private void validateSearchStatistics(String year, String month, String expectMessage) throws Exception {
		mvc.perform(get(LOCATION_PREFIX)
				.param(YEAR, year)
				.param(MONTH, month)
				.header(HttpHeaders.AUTHORIZATION, accessToken))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("messages").value(expectMessage));
	}

	private void initData() {
		saveIncome(userCategory, 100L, of(2021, 7, 1, 0, 0, 0));
		saveIncome(userCategory, 200L, of(2021, 6, 5, 0, 0, 0));
		saveIncome(userCategory, 700L, of(2021, 5, 13, 0, 0, 0));

		saveExpenditure(userCategory, 100L, of(2021, 7, 6, 0, 0, 0));
		saveExpenditure(userCategory, 100L, of(2021, 7, 9, 0, 0, 0));
		saveExpenditure(userCategory, 100L, of(2021, 1, 2, 0, 0, 0));
		saveExpenditure(userCategory, 100L, of(2021, 2, 1, 0, 0, 0));
	}
}
