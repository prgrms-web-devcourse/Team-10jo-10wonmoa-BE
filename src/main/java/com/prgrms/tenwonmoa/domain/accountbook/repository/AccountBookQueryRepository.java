package com.prgrms.tenwonmoa.domain.accountbook.repository;

import static com.prgrms.tenwonmoa.domain.accountbook.QExpenditure.*;
import static com.prgrms.tenwonmoa.domain.accountbook.QIncome.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.prgrms.tenwonmoa.domain.accountbook.dto.FindAccountDayResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindMonthSumResponse;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomImpl;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AccountBookQueryRepository {

	private final JPAQueryFactory queryFactory;

	public PageCustomImpl<FindAccountDayResponse> findDailyAccount(Long userId, PageCustomRequest pageRequest,
		LocalDate date) {

		// 어떻게 할 수 있을까...
		// 페이징 날짜 10개를 보내줘야해.
		// 날짜가 없으면 건너 뛰어도 돼...
		// expenditure와 income을 불러오는 기준이 너무 애매하다...

		// 둘다 날짜가 있다.
		// 둘 중 하나가 날짜가 있다
		// 둘 다 날짜가 없다 -> 데이터 안보내주면 돼
		// 이제 이 두 개 데이터를 어떻게 할 것이냐가 문젠데...
		List<FindAccountDayResponse> dayAccounts;

		return new PageCustomImpl<FindAccountDayResponse>(pageRequest.getPage(), pageRequest.getPage() + 1, null);
	}

	public FindMonthSumResponse findMonthSum(Long userId, LocalDate monthTime) {

		Long monthIncome = queryFactory.select(income.amount.sum())
			.from(income)
			.groupBy(income.registerDate.yearMonth())
			.where(income.registerDate.year().eq(monthTime.getYear()),
				income.registerDate.month().eq(monthTime.getMonthValue()),
				income.user.id.eq(userId)
			)
			.fetchOne();

		Long monthExpenditure = queryFactory.select(expenditure.amount.sum())
			.from(expenditure)
			.groupBy(expenditure.registerDate.yearMonth())
			.where(expenditure.registerDate.year().eq(monthTime.getYear()),
				expenditure.registerDate.month().eq(monthTime.getMonthValue()),
				expenditure.user.id.eq(userId)
			)
			.fetchOne();

		monthIncome = monthIncome == null ? 0L : monthIncome;
		monthExpenditure = monthExpenditure == null ? 0L : monthExpenditure;

		Long total = monthIncome - monthExpenditure;

		return new FindMonthSumResponse(monthIncome, monthExpenditure, total);
	}
}
