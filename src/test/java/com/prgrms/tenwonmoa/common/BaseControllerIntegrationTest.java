package com.prgrms.tenwonmoa.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BaseControllerIntegrationTest {

	@Autowired
	protected MockMvc mvc;

	protected ObjectMapper objectMapper = new ObjectMapper();

	protected String token;

	@BeforeEach
	void registerUserAndLogin() throws Exception {
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
		token = "Bearer " + responseJson.get("accessToken").asText();
	}
}
