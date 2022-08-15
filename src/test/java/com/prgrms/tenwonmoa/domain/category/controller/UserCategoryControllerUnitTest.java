package com.prgrms.tenwonmoa.domain.category.controller;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.prgrms.tenwonmoa.common.RestDocsConfig;
import com.prgrms.tenwonmoa.common.annotation.WithMockCustomUser;
import com.prgrms.tenwonmoa.config.JwtConfigure;
import com.prgrms.tenwonmoa.config.WebSecurityConfig;
import com.prgrms.tenwonmoa.domain.category.dto.FindCategoryResponse;
import com.prgrms.tenwonmoa.domain.category.dto.FindCategoryResponse.SingleCategoryResponse;
import com.prgrms.tenwonmoa.domain.category.service.FindUserCategoryService;
import com.prgrms.tenwonmoa.domain.category.service.UserCategoryService;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.security.jwt.filter.JwtAuthenticationFilter;
import com.prgrms.tenwonmoa.domain.user.service.UserService;

@WebMvcTest(controllers = UserCategoryController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfig.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtConfigure.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
	}
)
@MockBean(JpaMetamodelMappingContext.class)
@ExtendWith(RestDocumentationExtension.class)
@Import(RestDocsConfig.class)
@DisplayName("유저 카테고리 컨트롤러 테스트")
class UserCategoryControllerUnitTest {
	private static final String ENDPOINT_URL_PREFIX = "/api/v1/categories";

	@Autowired
	private RestDocumentationResultHandler restDocs;

	@MockBean
	private UserCategoryService userCategoryService;

	@MockBean
	private FindUserCategoryService findUserCategoryService;

	@MockBean
	private UserService userService;

	private MockMvc mockMvc;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final User user = createUser();

	@BeforeEach
	void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.apply(MockMvcRestDocumentation.documentationConfiguration(provider))
			.apply(springSecurity())
			.alwaysDo(restDocs)
			.addFilters(new CharacterEncodingFilter("UTF-8", true)) // 한글 깨짐 방지
			.build();

	}

	@Test
	@WithMockCustomUser
	void 카테고리_조회() throws Exception {
		String categoryType = "EXPENDITURE";
		given(findUserCategoryService.findUserCategories(anyLong(), eq(categoryType)))
			.willReturn(new FindCategoryResponse(
				List.of(new SingleCategoryResponse(1L, "식비", "EXPENDITURE"))));

		mockMvc.perform(get(ENDPOINT_URL_PREFIX).queryParam("kind", categoryType))
			.andExpect(status().isOk())
			.andDo(
				restDocs.document(
					requestParameters(
						parameterWithName("kind").description("카테고리 종류")
					),
					responseFields(
						fieldWithPath("categories[].id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
						fieldWithPath("categories[].name").type(JsonFieldType.STRING).description("카테고리 이름"),
						fieldWithPath("categories[].categoryType").type(JsonFieldType.STRING).description("카테고리 종류")
					)
				)
			);
	}

	@Test
	@WithMockCustomUser
	void 카테고리_등록() throws Exception {
		Long userCategoryId = 1L;
		String categoryType = "EXPENDITURE";
		String categoryName = "교통비";

		ObjectNode body = objectMapper.createObjectNode();
		body.put("categoryType", categoryType);
		body.put("name", categoryName);

		given(userCategoryService.createUserCategory(user, categoryType, categoryName)).willReturn(userCategoryId);

		mockMvc.perform(post(ENDPOINT_URL_PREFIX)
				.contentType(MediaType.APPLICATION_JSON)
				.content(body.toString())
				.with(csrf()))
			.andExpect(status().isCreated())
			.andDo(
				restDocs.document(
					requestFields(
						fieldWithPath("categoryType").type(JsonFieldType.STRING).description("카테고리 타입(수입/지출)"),
						fieldWithPath("name").type(JsonFieldType.STRING).description("카테고리 이름")
					),
					responseFields(
						fieldWithPath("id").type(JsonFieldType.NUMBER).description("생성된 카테고리 아이디")
					)
				)
			);
	}

	@Test
	@WithMockCustomUser
	void 카테고리_수정() throws Exception {
		Long userCategoryId = 1L;
		String updateName = "수정된 분류이름";

		ObjectNode body = objectMapper.createObjectNode();
		body.put("name", updateName);

		given(userService.findById(anyLong())).willReturn(user);
		given(userCategoryService.updateName(user, userCategoryId, updateName)).willReturn(updateName);

		mockMvc.perform(patch(ENDPOINT_URL_PREFIX + "/{userCategoryId}", userCategoryId)
				.with(csrf())
				.content(body.toString())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(
				restDocs.document(
					requestFields(
						fieldWithPath("name").type(JsonFieldType.STRING).description("업데이트할 카테고리 이름")
					),
					responseFields(
						fieldWithPath("name").type(JsonFieldType.STRING).description("업데이트 된 카테고리 이름")
					)
				)
			);
	}

	@Test
	@WithMockCustomUser
	void 카테고리_삭제() throws Exception {
		Long userCategoryId = 1L;

		given(userService.findById(anyLong())).willReturn(user);

		mockMvc.perform(
				RestDocumentationRequestBuilders.delete(ENDPOINT_URL_PREFIX + "/{userCategoryId}", userCategoryId)
					.with(csrf()))
			.andExpect(status().isNoContent())
			.andDo(
				restDocs.document(
					pathParameters(parameterWithName("userCategoryId").description("카테고리 아이디"))
				)
			);
		verify(userCategoryService).deleteUserCategory(user, userCategoryId);
	}
}
