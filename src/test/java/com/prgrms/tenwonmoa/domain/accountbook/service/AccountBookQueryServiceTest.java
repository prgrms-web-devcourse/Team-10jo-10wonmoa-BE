package com.prgrms.tenwonmoa.domain.accountbook.service;

import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prgrms.tenwonmoa.domain.accountbook.dto.condition.MonthCondition;
import com.prgrms.tenwonmoa.domain.accountbook.dto.condition.YearMonthCondition;
import com.prgrms.tenwonmoa.domain.accountbook.repository.AccountBookQueryRepository;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;

@DisplayName("AccountBookQuery 서비스 계층 단위 테스트")
@ExtendWith(MockitoExtension.class)
class AccountBookQueryServiceTest {

	@Mock
	private AccountBookQueryRepository accountBookQueryRepository;

	@InjectMocks
	private AccountBookQueryService accountBookQueryService;

	@Nested
	@DisplayName("일일상세내역 조회 중: ")
	class DescribeOfDailyAccountGet {

		private final Long authId = 1L;

		private final PageCustomRequest pageRequest = new PageCustomRequest(1, 10);

		private final YearMonthCondition condition = new YearMonthCondition(2022, 8);

		@Test
		public void 성공적으로_조회한다() {
			accountBookQueryService.findDailyAccountVer2(authId, pageRequest, condition);
			then(accountBookQueryRepository).should().findDailyAccountVer2(authId, pageRequest, condition);
		}
	}

	@Nested
	@DisplayName("월별합계 조회 중")
	class DescribeOfMonthSumGet {

		private final Long authId = 1L;

		private final LocalDate date = LocalDate.of(2022, 8, 1);

		@Test
		public void 성공적으로_조회한다() {
			accountBookQueryService.findMonthSum(authId, date);
			then(accountBookQueryRepository).should().findMonthSum(authId, date);
		}
	}

	@Nested
	@DisplayName("연간 합계 조회 중")
	class DescribeOfYearSum {

		private final Long authId = 1L;

		private final int year = 2022;

		@Test
		public void 성공적으로_조회한다() {
			accountBookQueryService.findYearSum(authId, year);
			then(accountBookQueryRepository).should().findYearSum(authId, year);
		}
	}

	@Nested
	@DisplayName("월별 상세 내역 조회 중")
	class DescribeOfMonthAccount {

		private final Long authId = 1L;

		MonthCondition condition = new MonthCondition(LocalDateTime.now(), 2022);

		@Test
		public void 성공적으로_조회한다() {
			accountBookQueryService.findMonthAccount(authId, condition);
			then(accountBookQueryRepository).should().findMonthAccount(authId, condition);
		}
	}

	@Nested
	@DisplayName("달력 조회 중")
	class DescribeOfCalendar {

		private final Long authId = 1L;

		private final YearMonthCondition condition = new YearMonthCondition(2022, 8);

		@Test
		public void 성공적으로_조회한다() {
			accountBookQueryService.findCalendarAccount(authId, condition);
			then(accountBookQueryRepository).should().findCalendarAccount(authId, condition);
		}
	}

}
