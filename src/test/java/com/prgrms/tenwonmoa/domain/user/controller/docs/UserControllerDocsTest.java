package com.prgrms.tenwonmoa.domain.user.controller.docs;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.tenwonmoa.common.annotation.WithMockCustomUser;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.controller.UserController;
import com.prgrms.tenwonmoa.domain.user.dto.CheckPasswordRequest;
import com.prgrms.tenwonmoa.domain.user.dto.CreateUserRequest;
import com.prgrms.tenwonmoa.domain.user.dto.LoginUserRequest;
import com.prgrms.tenwonmoa.domain.user.dto.RefreshTokenRequest;
import com.prgrms.tenwonmoa.domain.user.dto.TokenResponse;
import com.prgrms.tenwonmoa.domain.user.security.jwt.filter.JwtAuthenticationFilter;
import com.prgrms.tenwonmoa.domain.user.security.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.prgrms.tenwonmoa.domain.user.security.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.prgrms.tenwonmoa.domain.user.service.UserService;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@MockBean(classes = {JpaMetamodelMappingContext.class,
	JwtAuthenticationFilter.class,
	OAuth2AuthenticationSuccessHandler.class,
	OAuth2AuthenticationFailureHandler.class})
@DisplayName("유저 Docs 테스트")
class UserControllerDocsTest {
	private static final String BASE_URL = "/api/v1/users";

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

		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createUserRequest)))
			.andExpect(status().isCreated())
			.andDo(document("user-create",
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

		mockMvc.perform(get(BASE_URL)
				.header(HttpHeaders.AUTHORIZATION, "Bearer jwt.token.here"))
			.andExpect(status().isOk())
			.andDo(document("user-info",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("Jwt token")
				),
				responseFields(
					fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
				))
			);
	}

	@Test
	void 로그인_성공() throws Exception {
		User user = new User("test@test.com", "12345678", "lee");
		LoginUserRequest loginUserRequest = new LoginUserRequest(user.getEmail(), user.getPassword());
		TokenResponse tokenResponse = new TokenResponse("access-token", "refresh-token");

		given(userService.login(user.getEmail(), user.getPassword())).willReturn(tokenResponse);

		mockMvc.perform(post(BASE_URL + "/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginUserRequest)))
			.andExpect(status().isOk())
			.andDo(document("user-login",
				requestFields(
					fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
					fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
				),
				responseFields(
					fieldWithPath("accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
					fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰")
				)
			));
	}

	@Test
	void 토큰_재발급_성공() throws Exception {
		RefreshTokenRequest refreshTokenRequest =
			new RefreshTokenRequest("accessTokenValue", "refreshTokenValue");
		TokenResponse tokenResponse = new TokenResponse("newAccessTokenValue", "refreshTokenValue");

		given(userService.refresh(refreshTokenRequest.getAccessToken(), refreshTokenRequest.getRefreshToken()))
			.willReturn(tokenResponse);

		mockMvc.perform(post(BASE_URL + "/refresh")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(refreshTokenRequest)))
			.andExpect(status().isOk())
			.andDo(document("user-token-refresh",
				requestFields(
					fieldWithPath("accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
					fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰")
				),
				responseFields(
					fieldWithPath("accessToken").type(JsonFieldType.STRING).description("새로 발급한 액세스 토큰"),
					fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰")
				)
			));
	}

	@Test
	@WithMockCustomUser
	void 로그아웃_성공() throws Exception {
		User user = new User("test@test.com", "12345678", "lee");
		String accessToken = "accessToken";

		doNothing().when(userService).logout(user.getId(), accessToken);

		mockMvc.perform(post(BASE_URL + "/logout")
				.header("Authorization", accessToken))
			.andExpect(status().isOk())
			.andDo(document("user-logout",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("Jwt token")
				)));
	}

	@Test
	@WithMockCustomUser
	void 유저_삭제_성공() throws Exception {
		User user = new User("test@test.com", "12345678", "lee");
		String accessToken = "accessToken";

		doNothing().when(userService).deleteUser(user.getId());

		mockMvc.perform(delete(BASE_URL + "/delete")
				.header("Authorization", accessToken))
			.andExpect(status().isOk())
			.andDo(document("user-delete",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("Jwt token")
				)));
	}

	@Test
	@WithMockCustomUser
	void 비밀번호_확인_성공() throws Exception {
		User user = new User("test@test.com", "12345678", "lee");
		CheckPasswordRequest checkPasswordRequest = new CheckPasswordRequest(user.getPassword());
		String accessToken = "accessToken";

		given(userService.findById(any())).willReturn(user);
		doNothing().when(userService).checkPassword(user.getPassword(), checkPasswordRequest.getPassword());

		mockMvc.perform(post(BASE_URL + "/password/check")
				.header("Authorization", accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(checkPasswordRequest)))
			.andExpect(status().isOk())
			.andDo(document("user-check-password",
				requestHeaders(
					headerWithName(HttpHeaders.AUTHORIZATION).description("Jwt token")
				),
				requestFields(
					fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
				)));
	}

}
