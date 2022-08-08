package com.prgrms.tenwonmoa.domain.accountbook.controller.docs;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.tenwonmoa.common.annotation.WithMockCustomUser;
import com.prgrms.tenwonmoa.common.documentdto.FindStatisticsDataDoc;
import com.prgrms.tenwonmoa.common.documentdto.FindStatisticsResponseDoc;
import com.prgrms.tenwonmoa.config.JwtConfigure;
import com.prgrms.tenwonmoa.config.WebSecurityConfig;
import com.prgrms.tenwonmoa.domain.accountbook.controller.StatisticsController;
import com.prgrms.tenwonmoa.domain.accountbook.dto.statistics.FindStatisticsData;
import com.prgrms.tenwonmoa.domain.accountbook.dto.statistics.FindStatisticsResponse;
import com.prgrms.tenwonmoa.domain.accountbook.service.StatisticsService;
import com.prgrms.tenwonmoa.domain.user.security.jwt.filter.JwtAuthenticationFilter;

@WebMvcTest(controllers = StatisticsController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfig.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtConfigure.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
	}
)
@AutoConfigureRestDocs
@MockBean(JpaMetamodelMappingContext.class)
@DisplayName("통계 컨트롤러 테스트")
class StatisticsControllerTest {
	private static final List<String> INCOME_DEFAULT = List.of("용돈", "상여", "금융소득");
	private static final List<String> EXPENDITURE_DEFAULT = List.of("교통/차량", "문화생활", "마트/편의점");
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private StatisticsService statisticsService;

	private List<FindStatisticsData> incomes = List.of(new FindStatisticsData(INCOME_DEFAULT.get(0), 30L),
		new FindStatisticsData(INCOME_DEFAULT.get(1), 20L), new FindStatisticsData(INCOME_DEFAULT.get(2), 10L));
	private List<FindStatisticsData> expenditures = List.of(new FindStatisticsData(EXPENDITURE_DEFAULT.get(0), 45L),
		new FindStatisticsData(EXPENDITURE_DEFAULT.get(1), 44L),
		new FindStatisticsData(EXPENDITURE_DEFAULT.get(2), 43L));
	FindStatisticsResponse yearResponse = new FindStatisticsResponse(2022, null, 60L, 132L, incomes, expenditures);

	FindStatisticsResponse monthResponse = new FindStatisticsResponse(2022, 10, 60L, 132L, incomes, expenditures);

	private void setPercent() {
		incomes.get(0).setPercent(50.0);
		incomes.get(1).setPercent(33.33);
		incomes.get(2).setPercent(16.67);

		expenditures.get(0).setPercent(34.09);
		expenditures.get(1).setPercent(33.33);
		expenditures.get(2).setPercent(32.58);
	}

	@BeforeEach
	void init() {
		setPercent();
	}

	@Test
	@WithMockCustomUser
	void 연별_통계조회_성공() throws Exception {
		given(statisticsService.searchStatistics(any(), any(), any())).willReturn(yearResponse);

		mockMvc.perform(get("/api/v1/statistics").param("year", "2022"))
			.andExpect(status().isOk())
			.andDo(document("statistics-find-year",
				responseFields().andWithPrefix(FindStatisticsResponseDoc.INCOMES.getField(),
						FindStatisticsDataDoc.fieldDescriptors())
					.andWithPrefix(FindStatisticsResponseDoc.EXPENDITURES.getField(),
						FindStatisticsDataDoc.fieldDescriptors())
					.and(FindStatisticsResponseDoc.fieldDescriptorsYear())));
	}

	@Test
	@WithMockCustomUser
	void 월별_통계조회_성공() throws Exception {
		given(statisticsService.searchStatistics(any(), any(), any())).willReturn(monthResponse);

		mockMvc.perform(get("/api/v1/statistics").param("year", "2022").param("month", "10"))
			.andExpect(status().isOk())
			.andDo(document("statistics-find-month",
				responseFields().andWithPrefix(FindStatisticsResponseDoc.INCOMES.getField(),
						FindStatisticsDataDoc.fieldDescriptors())
					.andWithPrefix(FindStatisticsResponseDoc.EXPENDITURES.getField(),
						FindStatisticsDataDoc.fieldDescriptors())
					.and(FindStatisticsResponseDoc.fieldDescriptorsMonth())));
	}
}
