package com.prgrms.tenwonmoa.domain.user.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.tenwonmoa.common.annotation.WithMockCustomUser;
import com.prgrms.tenwonmoa.config.JwtConfigure;
import com.prgrms.tenwonmoa.config.WebSecurityConfig;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.dto.CreateUserRequest;
import com.prgrms.tenwonmoa.domain.user.security.jwt.filter.JwtAuthenticationFilter;
import com.prgrms.tenwonmoa.domain.user.service.UserService;

@WebMvcTest(controllers = UserController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfig.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtConfigure.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
	}
)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@MockBean(JpaMetamodelMappingContext.class)
@DisplayName("유저 컨트롤러 테스트")
class UserControllerTest {

	@MockBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;    // 테스트 실행을 위해 필요

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
			.andDo(MockMvcRestDocumentationWrapper.document("user-create",
				requestFields(
					fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
					fieldWithPath("username").type(JsonFieldType.STRING).description("사용자이름"),
					fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"))
			));
	}

	@Test
	@WithMockCustomUser
	void 회원_정보_조회_성공() throws Exception {
		User user = new User("test@test.com", "lee", "12345678");
		given(userService.findById(1L)).willReturn(user);

		mockMvc.perform(get("/api/v1/users")
				.header(HttpHeaders.AUTHORIZATION, "Bearer jwt.token.here"))
			.andExpect(status().isOk())
			.andDo(MockMvcRestDocumentationWrapper.document("user-info",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("Jwt token")
				),
				responseFields(
					fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
				))
			);
	}
}
