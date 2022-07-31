package com.prgrms.tenwonmoa.domain.user.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.tenwonmoa.config.JwtConfigure;
import com.prgrms.tenwonmoa.domain.user.dto.CreateUserRequest;
import com.prgrms.tenwonmoa.domain.user.service.UserService;

@WebMvcTest(UserController.class)
@AutoConfigureRestDocs
@MockBean(JpaMetamodelMappingContext.class)
@EnableConfigurationProperties(JwtConfigure.class)
class UserControllerTest {

	@MockBean
	private UserService userService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void 회원가입_성공() throws Exception {
		CreateUserRequest createUserRequest =
			new CreateUserRequest("test@test.com", "lee", "12345678");

		Long userId = 1L;
		given(userService.createUser(createUserRequest)).willReturn(userId);

		mockMvc.perform(post("/api/v1/users")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createUserRequest)))
			.andExpect(status().isCreated())
			.andDo(print())
			.andDo(document("user-create",
				requestFields(
					fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
					fieldWithPath("username").type(JsonFieldType.STRING).description("사용자이름"),
					fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"))
			));
	}

}
