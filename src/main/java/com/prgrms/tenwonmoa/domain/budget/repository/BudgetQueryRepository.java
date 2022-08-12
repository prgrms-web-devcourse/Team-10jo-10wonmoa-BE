package com.prgrms.tenwonmoa.domain.budget.repository;

import static com.prgrms.tenwonmoa.domain.accountbook.QExpenditure.*;
import static com.prgrms.tenwonmoa.domain.budget.QBudget.*;
import static com.prgrms.tenwonmoa.domain.category.QUserCategory.*;
import static com.querydsl.core.group.GroupBy.*;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.prgrms.tenwonmoa.domain.budget.dto.FindBudgetByRegisterDate;
import com.prgrms.tenwonmoa.domain.budget.dto.FindBudgetData;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BudgetQueryRepository {
	private static final String NEED_TO_YEAR_WHEN_MONTH = "월 조건은 년도가 반드시 입력되어야 합니다.";

	private static final NumberPath<Long> AMOUNT = Expressions.numberPath(Long.class, "amount");

	private final JPAQueryFactory queryFactory;

	public List<FindBudgetData> searchUserCategoriesWithBudget(Long userId, YearMonth registerDate) {
		return queryFactory.select(Projections.constructor(FindBudgetData.class,
				userCategory.id,
				userCategory.category.name.as("categoryName"),
				budget.amount.coalesce(0L).as("amount")
			))
			.from(budget)
			.rightJoin(budget.userCategory, userCategory)
			.on(budget.registerDate.eq(registerDate), budget.user.id.eq(userId))
			.where(userCategory.user.id.eq(userId))
			.orderBy(AMOUNT.desc())
			.fetch();
	}

	public Map<Long, Long> searchExpendituresExistBudget(Long userId, Integer year, Integer month) {
		return queryFactory
			.select(
				expenditure.userCategory.id,
				expenditure.amount
			)
			.from(expenditure)
			.where(
				expenditure.user.id.eq(userId),
				yearMonthEqExpenditure(year, month),
				expenditure.userCategory.id.in(
					JPAExpressions
						.select(budget.userCategory.id)
						.from(budget)
						.where(yearMonthEqBudget(year, month), budget.amount.gt(0))
				)
			)
			.transform(
				groupBy(expenditure.userCategory.id).as(sum(expenditure.amount))
			);
	}

	public List<FindBudgetByRegisterDate> searchBudgetByRegisterDate(Long userId, Integer year, Integer month) {
		return queryFactory
			.select(Projections.constructor(FindBudgetByRegisterDate.class,
				budget.userCategory.id,
				budget.userCategory.category.name,
				budget.amount.sum()
			))
			.from(budget)
			.where(
				budget.user.id.eq(userId),
				yearMonthEqBudget(year, month),
				budget.amount.gt(0)
			)
			.groupBy(budget.userCategory.id, budget.userCategory.category.name)
			.fetch();
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

	private BooleanExpression yearMonthEqBudget(Integer year, Integer month) {
		if (month != null) {
			return budget.registerDate.eq(YearMonth.of(year, month));
		}
		return budget.registerDate.between(YearMonth.of(year, 1), YearMonth.of(year, 12));
	}
}
