package com.prgrms.tenwonmoa.domain.accountbook.repository;

import static com.prgrms.tenwonmoa.domain.accountbook.QExpenditure.*;
import static com.prgrms.tenwonmoa.domain.accountbook.QIncome.*;
import static com.prgrms.tenwonmoa.domain.category.QCategory.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.prgrms.tenwonmoa.domain.accountbook.dto.statistics.FindStatisticsData;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StatisticsQueryRepository {
	private static final String NEED_TO_YEAR_WHEN_MONTH = "월 조건은 년도가 반드시 입력되어야 합니다.";
	private static final String NAME = "name";
	private static final String TOTAL = "total";
	private static final StringPath NAME_ALIAS = Expressions.stringPath(NAME);
	private static final NumberPath<Long> TOTAL_ALIAS = Expressions.numberPath(Long.class, TOTAL);

	private final JPAQueryFactory queryFactory;

	public List<FindStatisticsData> searchIncomeByRegisterDate(Long userId, Integer year, Integer month) {
		return queryFactory.select(Projections.constructor(FindStatisticsData.class,
				category.name.coalesce(income.categoryName).as(NAME),
				income.amount.sum().as(TOTAL)
			))
			.from(income)
			.leftJoin(income.userCategory.category, category)
			.where(yearMonthEqIncome(year, month), income.user.id.eq(userId))
			.groupBy(NAME_ALIAS)
			.orderBy(TOTAL_ALIAS.desc())
			.fetch();
	}

	public List<FindStatisticsData> searchExpenditureByRegisterDate(Long userId, Integer year, Integer month) {
		return queryFactory.select(Projections.constructor(FindStatisticsData.class,
				category.name.coalesce(expenditure.categoryName).as(NAME),
				expenditure.amount.sum().as(TOTAL)
			))
			.from(expenditure)
			.leftJoin(expenditure.userCategory.category, category)
			.where(yearMonthEqExpenditure(year, month), expenditure.user.id.eq(userId))
			.groupBy(NAME_ALIAS)
			.orderBy(TOTAL_ALIAS.desc())
			.fetch();
	}

	private BooleanExpression yearMonthEqIncome(Integer year, Integer month) {
		if (month != null && year == null) {
			throw new IllegalArgumentException(NEED_TO_YEAR_WHEN_MONTH);
		}
		return yearEqIncome(year).and(monthEqIncome(month));
	}

	private BooleanExpression monthEqIncome(Integer month) {
		return month != null ? income.registerDate.month().eq(month) : null;
	}

	private BooleanExpression yearEqIncome(Integer year) {
		return year != null ? income.registerDate.year().eq(year) : null;
	}

	private BooleanExpression yearMonthEqExpenditure(Integer year, Integer month) {
		if (month != null && year == null) {
			throw new IllegalArgumentException(NEED_TO_YEAR_WHEN_MONTH);
		}
		return yearEqExpenditure(year).and(monthEqExpenditure(month));
	}

	private BooleanExpression monthEqExpenditure(Integer month) {
		return month != null ? expenditure.registerDate.month().eq(month) : null;
	}

	private BooleanExpression yearEqExpenditure(Integer year) {
		return year != null ? expenditure.registerDate.year().eq(year) : null;
	}
}
