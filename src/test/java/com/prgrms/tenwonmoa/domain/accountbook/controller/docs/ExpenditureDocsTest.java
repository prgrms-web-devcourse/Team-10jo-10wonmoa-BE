package com.prgrms.tenwonmoa.domain.accountbook.controller.docs;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.tenwonmoa.common.annotation.WithMockCustomUser;
import com.prgrms.tenwonmoa.config.JwtConfigure;
import com.prgrms.tenwonmoa.config.WebSecurityConfig;
import com.prgrms.tenwonmoa.domain.accountbook.controller.ExpenditureController;
import com.prgrms.tenwonmoa.domain.accountbook.dto.expenditure.CreateExpenditureRequest;
import com.prgrms.tenwonmoa.domain.accountbook.dto.expenditure.CreateExpenditureResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.expenditure.FindExpenditureResponse;
import com.prgrms.tenwonmoa.domain.accountbook.service.ExpenditureService;
import com.prgrms.tenwonmoa.domain.user.security.jwt.filter.JwtAuthenticationFilter;

@WebMvcTest(controllers = ExpenditureController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfig.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtConfigure.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
	}
)
@AutoConfigureRestDocs
@MockBean(JpaMetamodelMappingContext.class)
@DisplayName("지출 DOCS 테스트: ")
public class ExpenditureDocsTest {

	private static final String BASE_URL = "/api/v1/expenditures";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ExpenditureService expenditureService;
	
	@Nested
	@DisplayName("지출 등록 API 호출 중 ")
	class DescribeOfPostExpenditure {

		private final LocalDateTime registerDate = LocalDateTime.of(2022, 8, 12, 8, 11);

		private final Long amount = 1000L;

		private final String content = "식비";

		private final Long userCategoryId = 2L;

		private final CreateExpenditureRequest request = new CreateExpenditureRequest(
			registerDate,
			amount,
			content,
			userCategoryId
		);

		private final Long createdId = 1L;

		CreateExpenditureResponse response = new CreateExpenditureResponse(1L);

		@Test
		@WithMockCustomUser
		public void 성공적으로_지출을_생성_할_수_있다_201() throws Exception {
			given(expenditureService.createExpenditure(anyLong(), any(CreateExpenditureRequest.class)))
				.willReturn(response);

			mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf())
			).andDo(document("expenditure-create",
				requestFields(documentCreateExpenditureRequest()),
				responseFields(documentCreateExpenditureResponse())
			));
		}

		private List<FieldDescriptor> documentCreateExpenditureRequest() {
			return List.of(
				fieldWithPath("registerDate").type(STRING).description("지출 등록 날짜"),
				fieldWithPath("amount").type(NUMBER).description("금액"),
				fieldWithPath("content").type(STRING).description("내용"),
				fieldWithPath("userCategoryId").type(NUMBER).description("유저카테고리 아이디")
			);
		}

		private List<FieldDescriptor> documentCreateExpenditureResponse() {
			return List.of(fieldWithPath("id").type(NUMBER).description("생성된 지출 아이디"));
		}

	}

	@Nested
	@DisplayName("지출 상세 내역 API 호출 중")
	class DescribeOfGetExpenditure {

		private final Long expenditureId = 1L;

		private final FindExpenditureResponse response = new FindExpenditureResponse(
			1L,
			LocalDateTime.now(),
			20000L,
			"돈까스마시써",
			3L,
			"식비"
		);

		@Test
		@WithMockCustomUser
		public void 성공적으로_지출을_조회할_수_있다_200() throws Exception {
			given(expenditureService.findExpenditure(any(Long.class), any()))
				.willReturn(response);

			mockMvc.perform(get(BASE_URL + "/{expenditureId}", expenditureId))
				.andExpect(status().isOk())
				.andDo(document("expenditure-get",
					responseFields(
						documentFindExpenditureResponse()
					)));
		}

		private List<FieldDescriptor> documentFindExpenditureResponse() {
			return List.of(
				fieldWithPath("id").type(NUMBER).description("지출 ID"),
				fieldWithPath("registerDate").type(STRING).description("지출 등록 날짜"),
				fieldWithPath("amount").type(NUMBER).description("지출 금액"),
				fieldWithPath("content").type(STRING).description("내용"),
				fieldWithPath("userCategoryId").type(NUMBER).description("유저 카테고리 ID"),
				fieldWithPath("categoryName").type(STRING).description("카테고리 이름")
			);
		}
	}

}
