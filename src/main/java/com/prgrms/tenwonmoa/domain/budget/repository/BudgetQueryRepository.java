package com.prgrms.tenwonmoa.domain.budget.repository;

import static com.prgrms.tenwonmoa.domain.budget.QBudget.*;
import static com.prgrms.tenwonmoa.domain.category.QUserCategory.*;

import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.prgrms.tenwonmoa.domain.budget.dto.FindBudgetData;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BudgetQueryRepository {
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
			.on(budget.registerDate.eq(registerDate))
			.where(userCategory.user.id.eq(userId))
			.orderBy(AMOUNT.desc())
			.fetch();
	}
}
