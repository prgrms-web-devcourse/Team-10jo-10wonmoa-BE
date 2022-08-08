package com.prgrms.tenwonmoa.domain.accountbook.controller.intergration;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.common.BaseControllerIntegrationTest;

@DisplayName("검색 컨트롤러 통합 테스트")
@Transactional
class SearchAccountBookIntegrationTest extends BaseControllerIntegrationTest {

	@BeforeEach
	void setup() throws Exception {
		registerUserAndLogin();
	}

	@Test
	void 검색_Api() throws Exception {
		Long incomeCategoryId = 카테고리_등록("INCOME", "월급");
		Long expenditureCategoryId = 카테고리_등록("EXPENDITURE", "식비");
		수입_등록(incomeCategoryId, 10000L, "용돈", LocalDateTime.now());
		지출_등록(expenditureCategoryId, 5000L, "점심", LocalDateTime.now());

		String categories = String.join(",", String.valueOf(incomeCategoryId), String.valueOf(expenditureCategoryId));
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
			.andExpect(jsonPath("$.currentPage").value(1))
			.andExpect(jsonPath("$.nextPage").isEmpty())
			.andExpect(jsonPath("$.results", hasSize(1))
			);
	}

	@Test
	void 검색_Api_모든_카테고리로_조회() throws Exception {
		Long incomeCategoryId = 카테고리_등록("INCOME", "월급");
		Long expenditureCategoryId = 카테고리_등록("EXPENDITURE", "식비");
		수입_등록(incomeCategoryId, 10000L, "용돈", LocalDateTime.now());
		지출_등록(expenditureCategoryId, 5000L, "점심", LocalDateTime.now());

		String blankCategory = "";
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
			.andExpect(jsonPath("$.currentPage").value(1))
			.andExpect(jsonPath("$.nextPage").isEmpty())
			.andExpect(jsonPath("$.results", hasSize(2)));
	}

	@Test
	void 검색_Api_디폴트_조회() throws Exception {
		Long incomeCategoryId = 카테고리_등록("INCOME", "월급");
		Long expenditureCategoryId = 카테고리_등록("EXPENDITURE", "식비");
		수입_등록(incomeCategoryId, 10000L, "용돈", LocalDateTime.now());
		지출_등록(expenditureCategoryId, 5000L, "점심", LocalDateTime.now());

		mvc.perform(get("/api/v1/account-book/search")
				.header(HttpHeaders.AUTHORIZATION, accessToken))
			.andExpect(jsonPath("$.incomeSum").value(10000L))
			.andExpect(jsonPath("$.expenditureSum").value(5000L))
			.andExpect(jsonPath("$.currentPage").value(1))
			.andExpect(jsonPath("$.nextPage").isEmpty())
			.andExpect(jsonPath("$.results", hasSize(2)));
	}
}
