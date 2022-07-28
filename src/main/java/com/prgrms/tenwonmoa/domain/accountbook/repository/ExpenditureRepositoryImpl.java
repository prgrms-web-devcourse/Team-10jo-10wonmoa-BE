package com.prgrms.tenwonmoa.domain.accountbook.repository;

import static com.prgrms.tenwonmoa.domain.accountbook.QExpenditure.*;

import java.time.LocalDate;
import java.util.List;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExpenditureRepositoryImpl implements ExpenditureCustomRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<Expenditure> findByRegisterDate(Long userId, LocalDate localDate) {

		List<Expenditure> expenditures = queryFactory.select(expenditure)
			.from(expenditure)
			.where(
				expenditure.user.id.eq(userId),
				expenditure.registerDate.eq(localDate)
			)
			.orderBy(expenditure.createdAt.desc())
			.fetch();

		return expenditures;
	}
}
