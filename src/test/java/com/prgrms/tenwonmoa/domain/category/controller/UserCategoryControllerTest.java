package com.prgrms.tenwonmoa.domain.category.controller;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.prgrms.tenwonmoa.common.RestDocsConfig;
import com.prgrms.tenwonmoa.domain.category.service.UserCategoryService;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.service.UserService;

@WebMvcTest(controllers = UserCategoryController.class)
@MockBean(JpaMetamodelMappingContext.class)
@ExtendWith(RestDocumentationExtension.class)
@Import(RestDocsConfig.class)
@DisplayName("유저 카테고리 컨트롤러 테스트")
class UserCategoryControllerTest {
	private static final String ENDPOINT_URL_PREFIX = "/api/v1/categories/";

	@Autowired
	private RestDocumentationResultHandler restDocs;

	@MockBean
	private UserCategoryService userCategoryService;

	@MockBean
	private UserService userService;

	private MockMvc mockMvc;

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
	void 카테고리_삭제() throws Exception {
		Long userCategoryId = 1L;

		given(userService.findById(anyLong())).willReturn(user);

		mockMvc.perform(
				RestDocumentationRequestBuilders.delete(ENDPOINT_URL_PREFIX + "{userCategoryId}", userCategoryId))
			.andExpect(status().isNoContent())
			.andDo(
				restDocs.document(
					pathParameters(parameterWithName("userCategoryId").description("카테고리 아이디"))
				)
			);
		verify(userCategoryService).deleteUserCategory(user, userCategoryId);
	}
}
