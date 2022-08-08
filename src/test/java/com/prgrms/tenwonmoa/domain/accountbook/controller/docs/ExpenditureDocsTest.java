package com.prgrms.tenwonmoa.domain.accountbook.controller.docs;

import static com.prgrms.tenwonmoa.exception.message.Message.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.tenwonmoa.common.annotation.WithMockCustomUser;
import com.prgrms.tenwonmoa.common.documentdto.ErrorResponseDoc;
import com.prgrms.tenwonmoa.domain.accountbook.controller.ExpenditureController;
import com.prgrms.tenwonmoa.domain.accountbook.dto.expenditure.CreateExpenditureRequest;
import com.prgrms.tenwonmoa.domain.accountbook.dto.expenditure.CreateExpenditureResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.expenditure.FindExpenditureResponse;
import com.prgrms.tenwonmoa.domain.accountbook.service.ExpenditureService;
import com.prgrms.tenwonmoa.domain.user.security.jwt.filter.JwtAuthenticationFilter;
import com.prgrms.tenwonmoa.exception.UnauthorizedUserException;

@WebMvcTest(controllers = ExpenditureController.class)
@AutoConfigureMockMvc(addFilters = false)
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
	private JwtAuthenticationFilter jwtAuthenticationFilter;    // 테스트 실행을 위해 필요

	@MockBean
	private ExpenditureService expenditureService;

	private final Long authId = 1L;

	private final Long noAuthId = 2L;

	private static final Long MAX_AMOUNT = 1000000000000L;

	private static final String MAX_CONTENT = "이것은50글자를넘습니다.이것은50글자를넘습니다.이것은50글자를넘습니다.이것은50글자를넘습니다.";

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
		public void 등록일자가_null일경우_400() throws Exception {
			CreateExpenditureRequest wrongResult = new CreateExpenditureRequest(
				null,
				amount,
				content,
				userCategoryId
			);

			mockMvc.perform(post(BASE_URL)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(wrongResult))
				)
				.andExpect(status().isBadRequest())
				.andDo(document("expenditure-post-register-date-null",
					responseFields(
						ErrorResponseDoc.fieldDescriptors()
					)
				));
		}

		@Test
		@WithMockCustomUser
		public void 금액이_null일경우_400() throws Exception {
			CreateExpenditureRequest wrongResult = new CreateExpenditureRequest(
				registerDate,
				null,
				content,
				userCategoryId
			);

			mockMvc.perform(post(BASE_URL)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(wrongResult))
				)
				.andExpect(status().isBadRequest())
				.andDo(document("expenditure-post-amount-null",
					responseFields(
						ErrorResponseDoc.fieldDescriptors()
					)
				));
		}

		@Test
		@WithMockCustomUser
		public void 금액의_범위가_0원보다_작을경우_400() throws Exception {

			CreateExpenditureRequest wrongResult = new CreateExpenditureRequest(
				registerDate,
				-1L,
				content,
				userCategoryId
			);

			mockMvc.perform(post(BASE_URL)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(wrongResult))
				)
				.andExpect(status().isBadRequest())
				.andDo(document("expenditure-post-amount-min-error",
					responseFields(
						ErrorResponseDoc.fieldDescriptors()
					)
				));
		}

		@Test
		@WithMockCustomUser
		public void 금액의_범위가_1조보다_클경우_400() throws Exception {

			CreateExpenditureRequest wrongResult = new CreateExpenditureRequest(
				registerDate,
				MAX_AMOUNT + 1,
				content,
				userCategoryId
			);

			mockMvc.perform(post(BASE_URL)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(wrongResult))
				)
				.andExpect(status().isBadRequest())
				.andDo(document("expenditure-post-amount-max-error",
					responseFields(
						ErrorResponseDoc.fieldDescriptors()
					)
				));
		}

		@Test
		@WithMockCustomUser
		public void 내용의길이가_50을넘길경우_400() throws Exception {
			CreateExpenditureRequest wrongResult = new CreateExpenditureRequest(
				registerDate,
				amount,
				MAX_CONTENT,
				userCategoryId
			);

			mockMvc.perform(post(BASE_URL)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(wrongResult))
				)
				.andExpect(status().isBadRequest())
				.andDo(document("expenditure-post-content-max-error",
					responseFields(
						ErrorResponseDoc.fieldDescriptors()
					)
				));

		}

		@Test
		@WithMockCustomUser
		public void 유저카테고리아이디가_null일경우_400() throws Exception {
			CreateExpenditureRequest wrongResult = new CreateExpenditureRequest(
				registerDate,
				MAX_AMOUNT + 1,
				content,
				null
			);

			mockMvc.perform(post(BASE_URL)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(wrongResult))
				)
				.andExpect(status().isBadRequest())
				.andDo(document("expenditure-post-user-category-id-null",
					responseFields(
						ErrorResponseDoc.fieldDescriptors()
					)
				));
		}

		@Test
		@WithMockCustomUser
		public void 인증받지않은_사용자일경우_403() throws Exception {
			given(expenditureService.createExpenditure(any(), any(CreateExpenditureRequest.class)))
				.willThrow(new UnauthorizedUserException(NO_AUTHENTICATION.getMessage()));

			mockMvc.perform(post(BASE_URL)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
				)
				.andExpect(status().isForbidden())
				.andDo(document("expenditure-create-forbidden",
					responseFields(
						ErrorResponseDoc.fieldDescriptors()
					)
				));
		}

		@Test
		@WithMockCustomUser
		public void 성공적으로_지출을_생성_할_수_있다_201() throws Exception {
			given(expenditureService.createExpenditure(anyLong(), any(CreateExpenditureRequest.class)))
				.willReturn(response);

			mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
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
		public void 권한이없는_사용자일경우_403() throws Exception {
			given(expenditureService.findExpenditure(any(Long.class), any()))
				.willThrow(new UnauthorizedUserException(NO_AUTHENTICATION.getMessage()));

			mockMvc.perform(get(BASE_URL + "/{expenditureId}", expenditureId))
				.andExpect(status().isForbidden())
				.andDo(document("expenditure-get-forbidden",
					responseFields(
						ErrorResponseDoc.fieldDescriptors()
					)));
		}

		@Test
		@WithMockCustomUser
		public void 없는_지출일경우_400() throws Exception {
			given(expenditureService.findExpenditure(any(Long.class), any()))
				.willThrow(new NoSuchElementException(EXPENDITURE_NOT_FOUND.getMessage()));

			mockMvc.perform(get(BASE_URL + "/{expenditureId}", expenditureId))
				.andExpect(status().isBadRequest())
				.andDo(document("expenditure-get-forbidden",
					responseFields(
						ErrorResponseDoc.fieldDescriptors()
					)));
		}

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
