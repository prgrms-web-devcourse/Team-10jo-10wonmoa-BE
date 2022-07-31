package com.prgrms.tenwonmoa.domain.accountbook.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.prgrms.tenwonmoa.domain.accountbook.dto.FindAccountDayResponse;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomImpl;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AccountBookQueryRepository {

	private final JPAQueryFactory queryFactory;

	public PageCustomImpl<FindAccountDayResponse> findDailyAccount(Long userId, PageCustomRequest pageRequest,
		LocalDateTime registeredMonth) {

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

}
