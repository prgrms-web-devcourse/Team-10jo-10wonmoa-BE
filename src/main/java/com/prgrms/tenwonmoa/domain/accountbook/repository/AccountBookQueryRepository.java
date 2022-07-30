package com.prgrms.tenwonmoa.domain.accountbook.repository;

import org.springframework.stereotype.Repository;

import com.prgrms.tenwonmoa.domain.accountbook.dto.FindAccountDayResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.PageCustomImpl;
import com.prgrms.tenwonmoa.domain.accountbook.dto.PageCustomRequest;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AccountBookQueryRepository {

	private final JPAQueryFactory queryFactory;

	public PageCustomImpl<FindAccountDayResponse> findDailyAccount(PageCustomRequest pageRequest, int month) {
		return null;
	}
}
