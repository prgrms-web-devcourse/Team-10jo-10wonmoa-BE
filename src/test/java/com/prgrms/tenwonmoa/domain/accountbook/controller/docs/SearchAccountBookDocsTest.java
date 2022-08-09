package com.prgrms.tenwonmoa.domain.accountbook.controller.docs;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.prgrms.tenwonmoa.domain.accountbook.dto.FindAccountBookResponse.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

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
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import com.prgrms.tenwonmoa.common.annotation.WithMockCustomUser;
import com.prgrms.tenwonmoa.config.JwtConfigure;
import com.prgrms.tenwonmoa.config.WebSecurityConfig;
import com.prgrms.tenwonmoa.domain.accountbook.controller.SearchAccountBookController;
import com.prgrms.tenwonmoa.domain.accountbook.dto.AccountBookItem;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindAccountBookResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.service.SearchAccountBookCmd;
import com.prgrms.tenwonmoa.domain.accountbook.service.SearchAccountBookService;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.service.FindUserCategoryService;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;
import com.prgrms.tenwonmoa.domain.user.security.jwt.filter.JwtAuthenticationFilter;

@WebMvcTest(controllers = SearchAccountBookController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfig.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtConfigure.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
	}
)
@MockBean(JpaMetamodelMappingContext.class)
@DisplayName("가계부 검색 DOCS 테스트")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class SearchAccountBookDocsTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SearchAccountBookService accountBookService;

	@MockBean
	private FindUserCategoryService userCategoryService;

	@Test
	@WithMockCustomUser
	void 지출_수입_검색() throws Exception {
		PageCustomRequest pageRequest = new PageCustomRequest(1, 1);
		FindAccountBookResponse<AccountBookItem> response = of(
			pageRequest,
			List.of(
				new AccountBookItem(1L, CategoryType.EXPENDITURE.name(), 10000L, "점심", "식비", LocalDateTime.now()),
				new AccountBookItem(1L, CategoryType.INCOME.name(), 50000L, "용돈", "용돈", LocalDateTime.now())),
			50000L, 10000L);

		given(accountBookService.searchAccountBooks(anyLong(), any(SearchAccountBookCmd.class), any(
			PageCustomRequest.class))).willReturn(response);

		mockMvc.perform(get("/api/v1/account-book/search")
				.param("categories", "1,2,3")
				.param("minprice", "1000")
				.param("maxprice", "50000")
				.param("start", "2022-08-01")
				.param("end", "2022-08-10")
				.param("content", "점심")
				.param("size", String.valueOf(pageRequest.getSize()))
				.param("page", String.valueOf(pageRequest.getPage())))
			.andExpect(status().isOk())
			.andDo(
				document("search-account-book",
					Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
					Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
					requestParameters(
						parameterWithName("categories").description("유저카테고리 아이디"),
						parameterWithName("minprice").description("최소 가격"),
						parameterWithName("maxprice").description("최대 가격"),
						parameterWithName("start").description("시작 등록일"),
						parameterWithName("end").description("종료 등록일"),
						parameterWithName("content").description("지출, 수입의 내용"),
						parameterWithName("size").description("페이지의 사이즈"),
						parameterWithName("page").description("페이지 번호")
					),
					responseFields(
						fieldWithPath("incomeSum").type(JsonFieldType.NUMBER).description("수입의 총합"),
						fieldWithPath("expenditureSum").type(JsonFieldType.NUMBER).description("지출의 총합"),
						fieldWithPath("totalSum").type(JsonFieldType.NUMBER).description("수입,지출 포함 총합"),
						fieldWithPath("currentPage").type(JsonFieldType.NUMBER).description("현재 페이지"),
						fieldWithPath("nextPage").type(JsonFieldType.NUMBER).description("다음 페이지"),
						fieldWithPath("results[]").type(JsonFieldType.ARRAY).description("검색된 지출, 수입 데이터"),
						fieldWithPath("results[].registerTime").type(JsonFieldType.STRING).description("등록일"),
						fieldWithPath("results[].amount").type(JsonFieldType.NUMBER).description("금액"),
						fieldWithPath("results[].content").type(JsonFieldType.STRING).description("내용"),
						fieldWithPath("results[].id").type(JsonFieldType.NUMBER).description("지출 or 수입의 아이디"),
						fieldWithPath("results[].type").type(JsonFieldType.STRING).description("지출 or 수입 종류"),
						fieldWithPath("results[].categoryName").type(JsonFieldType.STRING).description("카테고리 이름")
					)
				)
			);
	}
}
