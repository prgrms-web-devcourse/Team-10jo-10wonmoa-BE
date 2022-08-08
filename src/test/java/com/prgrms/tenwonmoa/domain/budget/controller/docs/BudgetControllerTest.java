package com.prgrms.tenwonmoa.domain.budget.controller.docs;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.tenwonmoa.common.annotation.WithMockCustomUser;
import com.prgrms.tenwonmoa.common.documentdto.CreateBudgetRequestDoc;
import com.prgrms.tenwonmoa.config.JwtConfigure;
import com.prgrms.tenwonmoa.config.WebSecurityConfig;
import com.prgrms.tenwonmoa.domain.budget.controller.BudgetController;
import com.prgrms.tenwonmoa.domain.budget.dto.CreateBudgetRequest;
import com.prgrms.tenwonmoa.domain.budget.service.BudgetTotalService;
import com.prgrms.tenwonmoa.domain.user.security.jwt.filter.JwtAuthenticationFilter;

@WebMvcTest(controllers = BudgetController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfig.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtConfigure.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
	}
)
@AutoConfigureRestDocs
@MockBean(JpaMetamodelMappingContext.class)
@DisplayName("예산 컨트롤러 테스트")
class BudgetControllerTest {
	private static final String LOCATION_PREFIX = "/api/v1/budgets";

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private BudgetTotalService budgetTotalService;

	private CreateBudgetRequest createBudgetRequest = new CreateBudgetRequest(
		1000L, LocalDate.now(), 1L);

	@Test
	@WithMockCustomUser
	void 예산_등록_성공() throws Exception {
		Long createdId = 1L;
		given(budgetTotalService.createBudget(any(), any(CreateBudgetRequest.class)))
			.willReturn(createdId);

		mockMvc.perform(post(LOCATION_PREFIX)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createBudgetRequest))
				.with(csrf()))
			.andExpect(status().isCreated())
			.andDo(document("budget-create",
				requestFields(
					CreateBudgetRequestDoc.fieldDescriptors()
				),
				responseFields(
					fieldWithPath("id").type(JsonFieldType.NUMBER).description("생성된 예산 아이디")
				)
			));
	}

}
