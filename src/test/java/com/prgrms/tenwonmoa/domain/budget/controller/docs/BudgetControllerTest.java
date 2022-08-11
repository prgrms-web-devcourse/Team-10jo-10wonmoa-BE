package com.prgrms.tenwonmoa.domain.budget.controller.docs;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.YearMonth;
import java.util.List;

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
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.tenwonmoa.common.annotation.WithMockCustomUser;
import com.prgrms.tenwonmoa.common.documentdto.CreateOrUpdateBudgetRequestDoc;
import com.prgrms.tenwonmoa.common.documentdto.FindBudgetDataDoc;
import com.prgrms.tenwonmoa.config.JwtConfigure;
import com.prgrms.tenwonmoa.config.WebSecurityConfig;
import com.prgrms.tenwonmoa.domain.budget.controller.BudgetController;
import com.prgrms.tenwonmoa.domain.budget.dto.CreateOrUpdateBudgetRequest;
import com.prgrms.tenwonmoa.domain.budget.dto.FindBudgetData;
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

	private CreateOrUpdateBudgetRequest createOrUpdateBudgetRequest = new CreateOrUpdateBudgetRequest(
		1000L, YearMonth.now(), 1L);

	private List<FindBudgetData> findBudgets = List.of(
		new FindBudgetData(1L, "교통/차량", 1000L),
		new FindBudgetData(2L, "문화생활", 2000L),
		new FindBudgetData(3L, "마트/편의점", 3000L)
	);

	@Test
	@WithMockCustomUser
	void 예산_등록_성공() throws Exception {
		doNothing().when(budgetTotalService).createOrUpdateBudget(any(), any(CreateOrUpdateBudgetRequest.class));

		mockMvc.perform(put(LOCATION_PREFIX)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createOrUpdateBudgetRequest))
				.with(csrf()))
			.andExpect(status().isNoContent())
			.andDo(document("budget-create",
				requestFields(
					CreateOrUpdateBudgetRequestDoc.fieldDescriptors()
				)
			));
	}

	@Test
	@WithMockCustomUser
	void 월별_예산조회_성공() throws Exception {
		given(budgetTotalService.searchUserCategoriesWithBudget(any(), any()))
			.willReturn(findBudgets);

		mockMvc.perform(get(LOCATION_PREFIX)
				.param("registerDate", "2022-07"))
			.andExpect(status().isOk())
			.andDo(document("budget-findBy-registerDate",
				responseFields().andWithPrefix("budgets[].", FindBudgetDataDoc.fieldDescriptors())
			));
	}
}
