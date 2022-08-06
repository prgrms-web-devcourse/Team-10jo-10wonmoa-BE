package com.prgrms.tenwonmoa.domain.accountbook.controller;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.prgrms.tenwonmoa.common.BaseControllerIntegrationTest;
import com.prgrms.tenwonmoa.domain.accountbook.dto.income.CreateIncomeRequest;
import com.prgrms.tenwonmoa.domain.category.dto.CreateCategoryRequest;

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
		mvc.perform(MockMvcRequestBuilders.get("/api/v1/account-book/search")
				.header(HttpHeaders.AUTHORIZATION, accessToken)
				.param("categories", categories)
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
			.andExpect(jsonPath("$.results", hasSize(2))
			);
	}

	private void 지출_등록(Long userCategoryId, Long amount,
		String content, LocalDateTime registerDate) throws Exception {

		ObjectNode objectNode = objectMapper.createObjectNode();
		objectNode.put("registerDate", registerDate.toString());
		objectNode.put("amount", amount)
			.put("content", content)
			.put("userCategoryId", userCategoryId);

		mvc.perform(post("/api/v1/expenditures")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectNode.toString())
				.header(HttpHeaders.AUTHORIZATION, accessToken))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("id").exists());
	}

	private void 수입_등록(Long userCategoryId, Long amount,
		String content, LocalDateTime registerDate) throws Exception {
		CreateIncomeRequest request = new CreateIncomeRequest(registerDate, amount, content,
			userCategoryId);

		mvc.perform(post("/api/v1/incomes").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header(HttpHeaders.AUTHORIZATION, accessToken))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("id").exists());
	}

	private Long 카테고리_등록(String categoryType, String categoryName) throws Exception {
		//given
		CreateCategoryRequest createCategoryRequest
			= new CreateCategoryRequest(categoryType, categoryName);

		//when
		//then
		String response = mvc.perform(post("/api/v1/categories")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createCategoryRequest))
				.header(HttpHeaders.AUTHORIZATION, accessToken))
			.andExpectAll(
				status().isCreated(),
				jsonPath("$.id").exists()
			)
			.andReturn().getResponse().getContentAsString();
		return objectMapper.readTree(response).get("id").asLong();
	}
}