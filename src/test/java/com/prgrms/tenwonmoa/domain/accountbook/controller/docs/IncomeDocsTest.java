package com.prgrms.tenwonmoa.domain.accountbook.controller.docs;

import static com.prgrms.tenwonmoa.exception.message.Message.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ContextConfiguration;

import com.prgrms.tenwonmoa.common.BaseControllerUnitTest;
import com.prgrms.tenwonmoa.common.annotation.WithMockCustomUser;
import com.prgrms.tenwonmoa.common.documentdto.CreateIncomeRequestDoc;
import com.prgrms.tenwonmoa.common.documentdto.ErrorResponseDoc;
import com.prgrms.tenwonmoa.common.documentdto.FindIncomeResponseDoc;
import com.prgrms.tenwonmoa.common.documentdto.UpdateIncomeRequestDoc;
import com.prgrms.tenwonmoa.domain.accountbook.controller.IncomeController;
import com.prgrms.tenwonmoa.domain.accountbook.dto.income.CreateIncomeRequest;
import com.prgrms.tenwonmoa.domain.accountbook.dto.income.FindIncomeResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.income.UpdateIncomeRequest;
import com.prgrms.tenwonmoa.domain.accountbook.service.IncomeService;
import com.prgrms.tenwonmoa.domain.accountbook.service.IncomeTotalService;
import com.prgrms.tenwonmoa.exception.UnauthorizedUserException;
import com.prgrms.tenwonmoa.exception.handler.GlobalExceptionHandler;

@ContextConfiguration(classes = {IncomeController.class, GlobalExceptionHandler.class})
@DisplayName("수입 DOCS 테스트")
class IncomeDocsTest extends BaseControllerUnitTest {
	private static final String LOCATION_PREFIX = "/api/v1/incomes/";

	private final CreateIncomeRequest createIncomeRequest = new CreateIncomeRequest(
		LocalDateTime.now(),
		1000L,
		"content",
		1L
	);

	private final FindIncomeResponse findIncomeResponse = new FindIncomeResponse(
		1L,
		LocalDateTime.now(),
		1000L,
		"content",
		1L, "용돈"
	);
	private final UpdateIncomeRequest updateIncomeRequest = new UpdateIncomeRequest(LocalDateTime.now(),
		2000L,
		"updateContent",
		2L);
	@MockBean
	private IncomeTotalService incomeTotalService;
	@MockBean
	private IncomeService incomeService;

	@Test
	@WithMockCustomUser
	void 수입_권한_없음() throws Exception {
		given(incomeService.findIncome(any(Long.class), any()))
			.willThrow(new UnauthorizedUserException(NO_AUTHENTICATION.getMessage()));

		mockMvc.perform(get(LOCATION_PREFIX + "/{incomeId}",
				findIncomeResponse.getId())
				.with(csrf())
			)
			.andExpect(status().isForbidden())
			.andDo(restDocs.document(responseFields(
				ErrorResponseDoc.fieldDescriptors()
			)));
	}

	@Test
	@WithMockCustomUser
	void 수입_등록_성공() throws Exception {
		Long createdId = 1L;
		given(incomeTotalService.createIncome(any(), any(CreateIncomeRequest.class)))
			.willReturn(createdId);

		mockMvc.perform(post(LOCATION_PREFIX)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createIncomeRequest))
				.with(csrf())
			)
			.andExpect(status().isCreated())
			.andExpect(redirectedUrl(LOCATION_PREFIX + createdId))
			.andDo(restDocs.document(
				requestFields(
					CreateIncomeRequestDoc.fieldDescriptors()
				),
				responseFields(
					fieldWithPath("id").type(JsonFieldType.NUMBER).description("생성된 수입 아이디")
				)
			));
	}

	@Test
	@WithMockCustomUser
	void 수입_등록_실패() throws Exception {
		given(incomeTotalService.createIncome(any(), any(CreateIncomeRequest.class)))
			.willThrow(new NoSuchElementException(USER_CATEGORY_NOT_FOUND.getMessage()));

		mockMvc.perform(post(LOCATION_PREFIX)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createIncomeRequest))
				.with(csrf())
			)
			.andExpect(status().isNotFound())
			.andDo(restDocs.document(responseFields(
				ErrorResponseDoc.fieldDescriptors()
			)));
	}

	@Test
	@WithMockCustomUser
	void 수입_상세조회_성공() throws Exception {
		given(incomeService.findIncome(any(Long.class), any()))
			.willReturn(findIncomeResponse);

		mockMvc.perform(get(LOCATION_PREFIX + "/{incomeId}", findIncomeResponse.getId()))
			.andExpect(status().isOk())
			.andDo(restDocs.document(responseFields(
				FindIncomeResponseDoc.fieldDescriptors()
			)));
	}

	@Test
	@WithMockCustomUser
	void 수입_상세조회_실패() throws Exception {
		given(incomeService.findIncome(any(Long.class), any()))
			.willThrow(new NoSuchElementException(INCOME_NOT_FOUND.getMessage()));

		mockMvc.perform(get(LOCATION_PREFIX + "/{incomeId}", findIncomeResponse.getId()))
			.andExpect(status().isNotFound())
			.andDo(restDocs.document(responseFields(
				ErrorResponseDoc.fieldDescriptors()
			)));
	}

	@Test
	@WithMockCustomUser
	void 수입_수정_성공() throws Exception {
		Long incomeId = 1L;
		mockMvc.perform(put(LOCATION_PREFIX + "/{incomeId}", incomeId)
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf())
				.content(objectMapper.writeValueAsString(updateIncomeRequest))
			)
			.andExpect(status().isNoContent())
			.andDo(restDocs.document(requestFields(
				UpdateIncomeRequestDoc.fieldDescriptors()
			)));
	}

	@Test
	@WithMockCustomUser
	void 수입_수정_실패() throws Exception {
		willThrow(new NoSuchElementException(INCOME_NOT_FOUND.getMessage()))
			.given(incomeTotalService)
			.updateIncome(any(), any(Long.class), any(UpdateIncomeRequest.class));

		Long incomeId = 1L;
		mockMvc.perform(put(LOCATION_PREFIX + "/{incomeId}", incomeId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateIncomeRequest))
				.with(csrf())
			)
			.andExpect(status().isNotFound())
			.andDo(restDocs.document(responseFields(
				ErrorResponseDoc.fieldDescriptors()
			)));
	}

	@Test
	@WithMockCustomUser
	void 수입_삭제_성공() throws Exception {
		Long incomeId = 1L;
		mockMvc.perform(delete(LOCATION_PREFIX + "/{incomeId}", incomeId)
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf())
			)
			.andExpect(status().isNoContent())
			.andDo(restDocs.document());
	}
}
