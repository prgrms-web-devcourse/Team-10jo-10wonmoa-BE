package com.prgrms.tenwonmoa.domain.accountbook.controller.intergration;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import com.prgrms.tenwonmoa.common.BaseControllerIntegrationTest;
import com.prgrms.tenwonmoa.domain.accountbook.repository.ExpenditureRepository;
import com.prgrms.tenwonmoa.domain.accountbook.repository.IncomeRepository;
import com.prgrms.tenwonmoa.domain.category.repository.CategoryRepository;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;

@DisplayName("검색 컨트롤러 통합 테스트")
class SearchAccountBookIntegrationTest extends BaseControllerIntegrationTest {

	@Autowired
	private ExpenditureRepository expenditureRepository;

	@Autowired
	private IncomeRepository incomeRepository;

	@Autowired
	private UserCategoryRepository userCategoryRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setup() throws Exception {
		registerUserAndLogin();
	}

	@AfterEach
	void tearDown() {
		incomeRepository.deleteAllInBatch();
		expenditureRepository.deleteAllInBatch();
		userCategoryRepository.deleteAllInBatch();
		categoryRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
	}

	@Test
	void 검색_Api() throws Exception {
		//given
		Long incomeCategoryId = 카테고리_등록("INCOME", "월급");
		Long expenditureCategoryId = 카테고리_등록("EXPENDITURE", "식비");
		수입_등록(incomeCategoryId, 10000L, "용돈", LocalDateTime.now());
		지출_등록(expenditureCategoryId, 5000L, "점심", LocalDateTime.now());

		String categories = String.join(",", String.valueOf(incomeCategoryId), String.valueOf(expenditureCategoryId));

		//when
		//then
		mvc.perform(get("/api/v1/account-book/search")
				.header(HttpHeaders.AUTHORIZATION, accessToken)
				.param("categories", categories)
				.param("minprice", "0")
				.param("maxprice", "5000")
				.param("start", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
				.param("end", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
				.param("content", "")
				.param("size", "3")
				.param("page", "1"))
			.andExpect(jsonPath("$.incomeSum").value(0L))
			.andExpect(jsonPath("$.expenditureSum").value(5000L))
			.andExpect(jsonPath("$.totalSum").value(-5000L))
			.andExpect(jsonPath("$.currentPage").value(1))
			.andExpect(jsonPath("$.nextPage").isEmpty())
			.andExpect(jsonPath("$.results", hasSize(1)));
	}

	@Test
	void 검색_Api_모든_카테고리로_조회() throws Exception {
		//given
		Long incomeCategoryId = 카테고리_등록("INCOME", "월급");
		Long expenditureCategoryId = 카테고리_등록("EXPENDITURE", "식비");
		수입_등록(incomeCategoryId, 10000L, "용돈", LocalDateTime.now());
		지출_등록(expenditureCategoryId, 5000L, "점심", LocalDateTime.now());

		String blankCategory = "";

		//when
		//then
		mvc.perform(get("/api/v1/account-book/search")
				.header(HttpHeaders.AUTHORIZATION, accessToken)
				.param("categories", blankCategory)
				.param("minprice", "0")
				.param("maxprice", "10000")
				.param("start", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
				.param("end", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
				.param("content", "")
				.param("size", "3")
				.param("page", "1"))
			.andExpect(jsonPath("$.incomeSum").value(10000L))
			.andExpect(jsonPath("$.expenditureSum").value(5000L))
			.andExpect(jsonPath("$.totalSum").value(5000L))
			.andExpect(jsonPath("$.currentPage").value(1))
			.andExpect(jsonPath("$.nextPage").isEmpty())
			.andExpect(jsonPath("$.results", hasSize(2)));
	}

	@Test
	void 검색_Api_쿼리파라미터_없이_조회시_모두_조회() throws Exception {
		//given
		Long incomeCategoryId = 카테고리_등록("INCOME", "월급");
		Long expenditureCategoryId = 카테고리_등록("EXPENDITURE", "식비");
		수입_등록(incomeCategoryId, 10000L, "용돈", LocalDateTime.now());
		지출_등록(expenditureCategoryId, 5000L, "점심", LocalDateTime.now());

		//when
		//then
		mvc.perform(get("/api/v1/account-book/search")
				.header(HttpHeaders.AUTHORIZATION, accessToken))
			.andExpect(jsonPath("$.incomeSum").value(10000L))
			.andExpect(jsonPath("$.expenditureSum").value(5000L))
			.andExpect(jsonPath("$.totalSum").value(5000L))
			.andExpect(jsonPath("$.currentPage").value(1))
			.andExpect(jsonPath("$.nextPage").isEmpty())
			.andExpect(jsonPath("$.results", hasSize(2)));
	}
}
