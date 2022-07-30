package com.prgrms.tenwonmoa.domain.accountbook.controller;

import static com.prgrms.tenwonmoa.exception.message.Message.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.tenwonmoa.common.documentdto.CreateIncomeRequestDoc;
import com.prgrms.tenwonmoa.common.documentdto.ErrorResponseDoc;
import com.prgrms.tenwonmoa.common.documentdto.FindIncomeResponseDoc;
import com.prgrms.tenwonmoa.common.documentdto.UpdateIncomeRequestDoc;
import com.prgrms.tenwonmoa.domain.accountbook.dto.CreateIncomeRequest;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindIncomeResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.UpdateIncomeRequest;
import com.prgrms.tenwonmoa.domain.accountbook.service.IncomeService;
import com.prgrms.tenwonmoa.domain.accountbook.service.IncomeTotalService;

@WebMvcTest(controllers = IncomeController.class)
@AutoConfigureRestDocs
@MockBean(JpaMetamodelMappingContext.class)
@DisplayName("수입 컨트롤러 테스트")
class IncomeControllerTest {
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
		"용돈"
	);

	private UpdateIncomeRequest updateIncomeRequest = new UpdateIncomeRequest(LocalDateTime.now(),
		2000L,
		"updateContent",
		2L);

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private IncomeTotalService incomeTotalService;

	@MockBean
	private IncomeService incomeService;

	@Test
	void 수입_등록_성공() throws Exception {
		Long createdId = 1L;
		given(incomeTotalService.createIncome(any(Long.class), any(CreateIncomeRequest.class)))
			.willReturn(createdId);

		mockMvc.perform(post(LOCATION_PREFIX)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(createIncomeRequest))
		)
			.andExpect(status().isCreated())
			.andExpect(content().string(String.valueOf(createdId)))
			.andExpect(redirectedUrl(LOCATION_PREFIX + createdId))
			.andDo(document("income-create",
				requestFields(
					CreateIncomeRequestDoc.fieldDescriptors()
				)
			));
	}

	@Test
	void 수입_등록_실패() throws Exception {
		given(incomeTotalService.createIncome(any(Long.class), any(CreateIncomeRequest.class)))
			.willThrow(new NoSuchElementException(USER_CATEGORY_NOT_FOUND.getMessage()));

		mockMvc.perform(post(LOCATION_PREFIX)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(createIncomeRequest))
		)
			.andExpect(status().isBadRequest())
			.andDo(document("income-create-fail", responseFields(
				ErrorResponseDoc.fieldDescriptors()
			)));
	}

	@Test
	void 수입_상세조회_성공() throws Exception {
		given(incomeService.findIncome(any(Long.class), any(Long.class)))
			.willReturn(findIncomeResponse);

		mockMvc.perform(get(LOCATION_PREFIX + "/{incomeId}", findIncomeResponse.getId())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(createIncomeRequest))
		)
			.andExpect(status().isOk())
			.andDo(document("income-find-by-id", responseFields(
				FindIncomeResponseDoc.fieldDescriptors()
			)));
	}

	@Test
	void 수입_상세조회_실패() throws Exception {
		given(incomeService.findIncome(any(Long.class), any(Long.class)))
			.willThrow(new NoSuchElementException(INCOME_NOT_FOUND.getMessage()));

		mockMvc.perform(get(LOCATION_PREFIX + "/{incomeId}", findIncomeResponse.getId())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(createIncomeRequest))
		)
			.andExpect(status().isBadRequest())
			.andDo(document("income-find-by-id-fail", responseFields(
				ErrorResponseDoc.fieldDescriptors()
			)));
	}

	@Test
	void 수입_수정_성공() throws Exception {
		Long incomeId = 1L;
		mockMvc.perform(put(LOCATION_PREFIX + "/{incomeId}", incomeId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateIncomeRequest))
		)
			.andExpect(status().isNoContent())
			.andDo(document("income-update", requestFields(
				UpdateIncomeRequestDoc.fieldDescriptors()
			)));
	}

	@Test
	void 수입_수정_실패() throws Exception {
		willThrow(new NoSuchElementException(INCOME_NOT_FOUND.getMessage()))
			.given(incomeTotalService)
			.updateIncome(any(Long.class), any(Long.class), any(UpdateIncomeRequest.class));

		Long incomeId = 1L;
		mockMvc.perform(put(LOCATION_PREFIX + "/{incomeId}", incomeId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateIncomeRequest))
		)
			.andExpect(status().isBadRequest())
			.andDo(document("income-update-fail", responseFields(
				ErrorResponseDoc.fieldDescriptors()
			)));
	}
}
