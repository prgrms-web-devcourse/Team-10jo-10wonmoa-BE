package com.prgrms.tenwonmoa.domain.accountbook.controller;

import static com.prgrms.tenwonmoa.exception.message.Message.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
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
import com.prgrms.tenwonmoa.domain.accountbook.dto.CreateIncomeRequest;
import com.prgrms.tenwonmoa.domain.accountbook.service.IncomeTotalService;

@WebMvcTest(controllers = IncomeController.class)
@AutoConfigureRestDocs
@MockBean(JpaMetamodelMappingContext.class)
@DisplayName("수입 컨트롤러 테스트")
class IncomeControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private IncomeTotalService incomeTotalService;

	private static final String INCOME_CREATE_URI = "/api/v1/incomes/";

	private CreateIncomeRequest request = new CreateIncomeRequest(
		LocalDate.now(),
		1000L,
		"content",
		1L
	);

	@Test
	void 수입_등록_성공() throws Exception {
		Long createdId = 1L;
		given(incomeTotalService.createIncome(any(Long.class), any(CreateIncomeRequest.class)))
			.willReturn(createdId);

		mockMvc.perform(post("/api/v1/incomes")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
		)
			.andExpect(status().isCreated())
			.andExpect(content().string(String.valueOf(createdId)))
			.andExpect(redirectedUrl(INCOME_CREATE_URI + createdId))
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

		mockMvc.perform(post("/api/v1/incomes")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
		)
			.andExpect(status().isBadRequest())
			.andDo(document("income-create-fail", responseFields(
				ErrorResponseDoc.fieldDescriptors()
			)));
	}
}
