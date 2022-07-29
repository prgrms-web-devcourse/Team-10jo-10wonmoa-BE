package com.prgrms.tenwonmoa.domain.accountbook.repository;

import static com.prgrms.tenwonmoa.domain.accountbook.QExpenditure.*;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AccountBookQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Optional<Expenditure> findById(Long expenditureId) {
		return Optional.ofNullable(
			queryFactory.select(expenditure)
				.from(expenditure)
				.where(expenditure.id.eq(expenditureId))
				.fetchOne()
		);
	}
}
