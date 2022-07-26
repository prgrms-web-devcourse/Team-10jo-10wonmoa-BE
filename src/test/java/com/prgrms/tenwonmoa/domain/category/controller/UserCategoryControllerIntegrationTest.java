package com.prgrms.tenwonmoa.domain.category.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.common.BaseControllerIntegrationTest;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.dto.CreateCategoryRequest;
@Transactional
class UserCategoryControllerIntegrationTest extends BaseControllerIntegrationTest {

	@BeforeEach
	void init() throws Exception {
		registerUserAndLogin();
	}

	public static final String CATEGORY_URL_PREFIX = "/api/v1/categories";

	@Test
	void 수입_카테고리_조회_Api() throws Exception {
		mvc.perform(get(CATEGORY_URL_PREFIX)
				.param("kind", "income")
				.header(HttpHeaders.AUTHORIZATION, accessToken))
			.andExpectAll(
				status().isOk(),
				jsonPath("$.categories[0].categoryType").value("INCOME"),
				jsonPath("$.categories",
					hasSize(Category.DEFAULT_CATEGORY.get(CategoryType.INCOME).size()))
			);
	}

	@Test
	void 지출_카테고리_조회_Api() throws Exception {
		mvc.perform(get(CATEGORY_URL_PREFIX)
				.param("kind", "expenditure")
				.header(HttpHeaders.AUTHORIZATION, accessToken))
			.andExpectAll(
				status().isOk(),
				jsonPath("$.categories[0].categoryType").value("EXPENDITURE"),
				jsonPath("$.categories",
					hasSize(Category.DEFAULT_CATEGORY.get(CategoryType.EXPENDITURE).size()))
			);
	}

	@Test
	void 카테고리_등록_Api() throws Exception {
		//given
		CreateCategoryRequest createCategoryRequest
			= new CreateCategoryRequest("INCOME", "수입카테고리");

		//when
		//then
		mvc.perform(post(CATEGORY_URL_PREFIX)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createCategoryRequest))
				.header(HttpHeaders.AUTHORIZATION, accessToken))
			.andExpectAll(
				status().isCreated(),
				jsonPath("$.id").exists()
			);
	}

	@Test
	void 카테고리_수정_Api() throws Exception {
		//given
		CreateCategoryRequest createCategoryRequest
			= new CreateCategoryRequest("INCOME", "수입카테고리");

		String responseBody = mvc.perform(post(CATEGORY_URL_PREFIX)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createCategoryRequest))
				.header(HttpHeaders.AUTHORIZATION, accessToken))
			.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

		long categoryId = objectMapper.readTree(responseBody).get("id").asLong();

		//when
		//then
		mvc.perform(patch(CATEGORY_URL_PREFIX + "/{categoryId}", categoryId)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\" : \"수정된카테고리\"}")
				.header(HttpHeaders.AUTHORIZATION, accessToken))
			.andExpectAll(
				status().isOk(),
				jsonPath("$.name").value("수정된카테고리"));
	}

	@Test
	void 카테고리_삭제_Api() throws Exception {
		//given
		CreateCategoryRequest createCategoryRequest
			= new CreateCategoryRequest("INCOME", "수입카테고리");

		String responseBody = mvc.perform(post(CATEGORY_URL_PREFIX)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createCategoryRequest))
				.header(HttpHeaders.AUTHORIZATION, accessToken))
			.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

		long categoryId = objectMapper.readTree(responseBody).get("id").asLong();

		//when
		//then
		mvc.perform(delete(CATEGORY_URL_PREFIX + "/{categoryId}", categoryId)
				.header(HttpHeaders.AUTHORIZATION, accessToken))
			.andExpect(status().isNoContent());
	}
}
