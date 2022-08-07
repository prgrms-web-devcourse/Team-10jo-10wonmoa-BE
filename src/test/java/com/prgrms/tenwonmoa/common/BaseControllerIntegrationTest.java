package com.prgrms.tenwonmoa.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.prgrms.tenwonmoa.domain.accountbook.dto.income.CreateIncomeRequest;
import com.prgrms.tenwonmoa.domain.category.dto.CreateCategoryRequest;

@SpringBootTest
@AutoConfigureMockMvc
public class BaseControllerIntegrationTest {

	@Autowired
	protected MockMvc mvc;

	@Autowired
	protected ObjectMapper objectMapper;

	protected String accessToken;

	protected void registerUserAndLogin() throws Exception {
		회원_등록();
		로그인();
	}

	protected void 회원_등록() throws Exception {
		ObjectNode userData = objectMapper.createObjectNode();
		userData.put("email", "testuser@gmail.com");
		userData.put("username", "testuser");
		userData.put("password", "12345677");

		mvc.perform(post("/api/v1/users")
				.content(userData.toString())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated());
	}

	protected void 로그인() throws Exception {
		ObjectNode loginUser = objectMapper.createObjectNode();
		loginUser.put("email", "testuser@gmail.com");
		loginUser.put("password", "12345677");

		MockHttpServletResponse response = mvc.perform(post("/api/v1/users/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginUser.toString()))
			.andExpect(status().isOk()).andReturn().getResponse();

		String responseBody = response.getContentAsString();

		JsonNode responseJson = objectMapper.readTree(responseBody);
		accessToken = "Bearer " + responseJson.get("accessToken").asText();
	}

	protected void 지출_등록(Long userCategoryId, Long amount,
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

	protected void 수입_등록(Long userCategoryId, Long amount,
		String content, LocalDateTime registerDate) throws Exception {
		CreateIncomeRequest request = new CreateIncomeRequest(registerDate, amount, content,
			userCategoryId);

		mvc.perform(post("/api/v1/incomes").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header(HttpHeaders.AUTHORIZATION, accessToken))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("id").exists());
	}

	protected Long 카테고리_등록(String categoryType, String categoryName) throws Exception {
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
