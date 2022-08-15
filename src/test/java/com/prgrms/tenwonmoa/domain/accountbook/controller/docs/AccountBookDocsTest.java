package com.prgrms.tenwonmoa.domain.accountbook.controller.docs;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.prgrms.tenwonmoa.domain.category.CategoryType.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import org.springframework.test.web.servlet.MockMvc;

import com.prgrms.tenwonmoa.common.annotation.WithMockCustomUser;
import com.prgrms.tenwonmoa.config.JwtConfigure;
import com.prgrms.tenwonmoa.config.WebSecurityConfig;
import com.prgrms.tenwonmoa.domain.accountbook.controller.AccountBookQueryController;
import com.prgrms.tenwonmoa.domain.accountbook.dto.AccountBookItem;
import com.prgrms.tenwonmoa.domain.accountbook.dto.DateDetail;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindCalendarResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindDayAccountResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindMonthAccountResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindSumResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.MonthDetail;
import com.prgrms.tenwonmoa.domain.accountbook.service.AccountBookQueryService;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomImpl;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;
import com.prgrms.tenwonmoa.domain.user.security.jwt.filter.JwtAuthenticationFilter;

@WebMvcTest(controllers = AccountBookQueryController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfig.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtConfigure.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
	}
)
@AutoConfigureRestDocs
@MockBean(JpaMetamodelMappingContext.class)
@DisplayName("가계부 조회 DOCS 테스트: ")
public class AccountBookDocsTest {

	private static final String BASE_URL = "/api/v1/account-book";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AccountBookQueryService accountBookQueryService;

	private final Long authId = 1L;

	@Nested
	@DisplayName("일일상세내역 조회 중: ")
	class DescribeOfDailyAccountGet {

		private LocalDate date = LocalDate.now();

		private PageCustomRequest pageRequest = new PageCustomRequest(1, 10);

		List<AccountBookItem> date29Items = List.of(
			new AccountBookItem(
				1L, INCOME.toString(), 20000L, "수입29", "어디선가 벌었다", LocalDateTime.of(2022, 8, 29, 10, 11)
			),
			new AccountBookItem(
				2L, EXPENDITURE.toString(), 10000L, "지출29", "어디선가 썼다", LocalDateTime.of(2022, 8, 29, 10, 12)
			)
		);

		List<AccountBookItem> date28Items = List.of(
			new AccountBookItem(
				3L, INCOME.toString(), 20000L, "수입29", "어디선가 벌었다", LocalDateTime.of(2022, 8, 28, 10, 11)
			),
			new AccountBookItem(
				4L, EXPENDITURE.toString(), 10000L, "지출29", "어디선가 썼다", LocalDateTime.of(2022, 8, 28, 10, 12)
			)
		);

		List<FindDayAccountResponse> results = List.of(
			new FindDayAccountResponse(
				LocalDate.of(2022, 8, 29),
				20000L,
				10000L,
				date29Items
			),
			new FindDayAccountResponse(
				LocalDate.of(2022, 8, 28),
				20000L,
				10000L,
				date28Items
			)
		);

		private PageCustomImpl response = new PageCustomImpl<>(pageRequest, 4, results);

		@Test
		@WithMockCustomUser
		public void 성공적으로_조회할_수_있다_200() throws Exception {
			given(accountBookQueryService.findDailyAccountVer2(anyLong(), any(PageCustomRequest.class), any()))
				.willReturn(response);

			mockMvc.perform(get(BASE_URL + "/daily")
					.param("year", "2022")
					.param("month", "8")
					.param("page", "1")
					.param("size", "10")
				)
				.andExpect(status().isOk())
				.andDo(document("accountbook-daily-account",
					responseFields(
						fieldWithPath("currentPage").type(NUMBER).description("현재 페이지"),
						fieldWithPath("nextPage").type(NULL).description("다음 페이지"),
						fieldWithPath("totalElements").type(NUMBER).description("전체 결과 개수"),
						fieldWithPath("totalPages").type(NUMBER).description("페이지"),
						fieldWithPath("results[]").type(ARRAY).description("일일 조회된 지출, 수입 데이터"),
						fieldWithPath("results[].registerDate").type(STRING).description("등록일"),
						fieldWithPath("results[].incomeSum").type(NUMBER).description("수입 합계"),
						fieldWithPath("results[].expenditureSum").type(NUMBER).description("지출 합계"),
						fieldWithPath("results[].dayDetails[]").type(ARRAY).description("등록일에 대한 가계"),
						fieldWithPath("results[].dayDetails[].id").type(NUMBER).description("가계 Id"),
						fieldWithPath("results[].dayDetails[].type").type(STRING)
							.description("가계 종류(INCOME, EXPENDITURE)"),
						fieldWithPath("results[].dayDetails[].amount").type(NUMBER).description("가계 금액"),
						fieldWithPath("results[].dayDetails[].content").type(STRING).description("가계 내용"),
						fieldWithPath("results[].dayDetails[].categoryName").type(STRING).description("가계 category 이름"),
						fieldWithPath("results[].dayDetails[].registerTime").type(STRING).description("가계 등록 시간")
					)
				));
		}
	}

	@Nested
	@DisplayName("월별합계 조회 중")
	class DescribeOfMonthSumGet {

		private final FindSumResponse response = new FindSumResponse(100000L, 50000L);

		@Test
		@WithMockCustomUser
		public void 성공적으로_조회할_수_있다_200() throws Exception {
			given(accountBookQueryService.findMonthSum(anyLong(), any()))
				.willReturn(response);

			mockMvc.perform(get(BASE_URL + "/sum/month/{date}", "2022-08-01"))
				.andExpect(status().isOk())
				.andDo(document("accountbook-month-sum",
					responseFields(
						fieldWithPath("incomeSum").type(NUMBER).description("월별 수입 합계"),
						fieldWithPath("expenditureSum").type(NUMBER).description("월별 지출 합계"),
						fieldWithPath("totalSum").type(NUMBER).description("총 합계")
					))
				);
		}
	}

	@Nested
	@DisplayName("연간 합계 조회 중")
	class DescribeOfYearSum {

		private final int year = 2022;

		private final FindSumResponse response = new FindSumResponse(20000000000L, 50000000L);

		@Test
		@WithMockCustomUser
		public void 성공적으로_조회할_수_있다_200() throws Exception {
			given(accountBookQueryService.findYearSum(authId, year))
				.willReturn(response);

			mockMvc.perform(get(BASE_URL + "/sum/year/{year}", year))
				.andExpect(status().isOk())
				.andDo(document("accountbook-year-sum",
					responseFields(
						fieldWithPath("incomeSum").type(NUMBER).description("월별 수입 합계"),
						fieldWithPath("expenditureSum").type(NUMBER).description("월별 지출 합계"),
						fieldWithPath("totalSum").type(NUMBER).description("총 합계")
					))
				);
		}
	}

	@Nested
	@DisplayName("월별 상세 내역 조회 중")
	class DescribeOfMonthAccount {

		private final int year = 2022;

		private List<MonthDetail> results = new ArrayList<>();

		@Test
		@WithMockCustomUser
		public void 성공적으로_조회할_수_있다_200() throws Exception {
			addMonthDetail();
			FindMonthAccountResponse response = new FindMonthAccountResponse(results);

			given(accountBookQueryService.findMonthAccount(anyLong(), any()))
				.willReturn(response);

			mockMvc.perform(get(BASE_URL + "/month/{year}", year))
				.andExpect(status().isOk())
				.andDo(document("accountbook-month-account",
					responseFields(
						fieldWithPath("results[]").type(ARRAY).description("월별 상세내역 리스트"),
						fieldWithPath("results[].incomeSum").type(NUMBER).description("월별 수입 합계"),
						fieldWithPath("results[].expenditureSum").type(NUMBER).description("월별 지출 합계"),
						fieldWithPath("results[].totalSum").type(NUMBER).description("총 합계"),
						fieldWithPath("results[].month").type(NUMBER).description("해당 월")
					))
				);
		}

		private void addMonthDetail() {
			for (int i = 1; i <= 8; i++) {
				results.add(new MonthDetail(100000L, 50000L, i));
			}
		}
	}

	@Nested
	@DisplayName("달력 조회 중")
	class DescribeOfCalendar {

		private final int year = 2022;
		private final int month = 8;

		private List<DateDetail> results = new ArrayList<>();

		@Test
		@WithMockCustomUser
		public void 성공적으로_조회할_수_있다_200() throws Exception {
			addDateDetail();
			FindCalendarResponse response = new FindCalendarResponse(month, results);

			given(accountBookQueryService.findCalendarAccount(anyLong(), any()))
				.willReturn(response);

			mockMvc.perform(get(BASE_URL + "/calendar")
					.param("year", "2022")
					.param("month", "8")
				)
				.andExpect(status().isOk())
				.andDo(document("accountbook-calendar-account",
					responseFields(
						fieldWithPath("month").type(NUMBER).description("월"),
						fieldWithPath("results[]").type(ARRAY).description("달력 상세내역 리스트"),
						fieldWithPath("results[].date").type(STRING).description("해당 날짜"),
						fieldWithPath("results[].incomeSum").type(NUMBER).description("월별 수입 합계"),
						fieldWithPath("results[].expenditureSum").type(NUMBER).description("월별 지출 합계"),
						fieldWithPath("results[].totalSum").type(NUMBER).description("총 합계")
					))
				);

		}

		private void addDateDetail() {
			for (int i = 1; i <= 31; i++) {
				results.add(new DateDetail(LocalDate.of(year, month, i), 100000L, 50000L));
			}
		}
	}
}
