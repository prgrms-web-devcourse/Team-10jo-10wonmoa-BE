package com.prgrms.tenwonmoa.domain.accountbook.repository;

import static com.prgrms.tenwonmoa.domain.accountbook.QExpenditure.*;
import static com.prgrms.tenwonmoa.domain.accountbook.QIncome.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.prgrms.tenwonmoa.domain.accountbook.dto.DayDetail;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindDayAccountResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindMonthSumResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.QDayDetail;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomImpl;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AccountBookQueryRepository {

	private final JPAQueryFactory queryFactory;

	public PageCustomImpl<FindDayAccountResponse> findDailyAccount(Long userId, PageCustomRequest pageRequest,
		LocalDate date) {

		List<LocalDate> dates = getPageDate(pageRequest, userId, date);

		List<FindDayAccountResponse> responses = new ArrayList<>();

		dates.iterator().forEachRemaining(
			d -> responses.add(new FindDayAccountResponse(d, getDayIncomeAmount(d), getDayExpenditureAmount(d),
				getDayDetails(userId, d)))
		);

		Collections.sort(responses,
			(o1, o2) -> o2.getRegisterDate().getDayOfMonth() - o1.getRegisterDate().getDayOfMonth());

		return new PageCustomImpl<>(pageRequest.getPage(), pageRequest, responses);
	}

	public FindMonthSumResponse findMonthSum(Long userId, LocalDate month) {

		Long monthIncome = queryFactory.select(income.amount.sum())
			.from(income)
			.groupBy(income.registerDate.yearMonth())
			.where(income.registerDate.year().eq(month.getYear()),
				income.registerDate.month().eq(month.getMonthValue()),
				income.user.id.eq(userId)
			)
			.fetchOne();

		Long monthExpenditure = queryFactory.select(expenditure.amount.sum())
			.from(expenditure)
			.groupBy(expenditure.registerDate.yearMonth())
			.where(expenditure.registerDate.year().eq(month.getYear()),
				expenditure.registerDate.month().eq(month.getMonthValue()),
				expenditure.user.id.eq(userId)
			)
			.fetchOne();

		monthIncome = monthIncome == null ? 0L : monthIncome;
		monthExpenditure = monthExpenditure == null ? 0L : monthExpenditure;

		Long total = monthIncome - monthExpenditure;

		return new FindMonthSumResponse(monthIncome, monthExpenditure, total);
	}

	private List<DayDetail> getDayDetails(Long userId, LocalDate localDate) {
		int year = localDate.getYear();
		int month = localDate.getMonthValue();
		int day = localDate.getDayOfMonth();

		List<DayDetail> dayDetails = queryFactory.select(new QDayDetail(
				expenditure.id,
				expenditure.userCategory.category.categoryType.stringValue(),
				expenditure.amount,
				expenditure.content,
				expenditure.categoryName
			))
			.from(expenditure)
			.where(
				expenditure.user.id.eq(userId),
				expenditure.registerDate.year().eq(year),
				expenditure.registerDate.month().eq(month),
				expenditure.registerDate.dayOfMonth().eq(day)
			)
			.fetch();

		dayDetails.addAll(
			queryFactory.select(new QDayDetail(
					income.id,
					income.userCategory.category.categoryType.stringValue(),
					income.amount,
					income.content,
					income.categoryName
				))
				.from(income)
				.where(
					income.user.id.eq(userId),
					income.registerDate.year().eq(year),
					income.registerDate.month().eq(month),
					income.registerDate.dayOfMonth().eq(day)
				)
				.fetch()
		);

		dayDetails.sort(Comparator.comparing(DayDetail::getType));

		return dayDetails;
	}

	private List<LocalDate> getPageDate(PageCustomRequest pageCustomRequest, Long userId, LocalDate date) {

		long offset = pageCustomRequest.getOffset();
		int size = pageCustomRequest.getSize();
		int year = date.getYear();
		int month = date.getMonthValue();

		List<LocalDateTime> expenditureTimes = queryFactory.select(expenditure.registerDate)
			.from(expenditure)
			.where(
				expenditure.user.id.eq(userId),
				expenditure.registerDate.year().eq(year),
				expenditure.registerDate.month().eq(month)
			)
			.fetch();

		List<LocalDateTime> incomeTimes = queryFactory.select(income.registerDate)
			.from(income)
			.where(
				income.user.id.eq(userId),
				income.registerDate.year().eq(year),
				income.registerDate.month().eq(month)
			)
			.fetch();

		List<LocalDate> expenditureDate = expenditureTimes.stream()
			.map(LocalDateTime::toLocalDate)
			.collect(Collectors.toList());

		List<LocalDate> incomeDate = incomeTimes.stream()
			.map(LocalDateTime::toLocalDate)
			.collect(Collectors.toList());

		expenditureDate.addAll(incomeDate);

		List<LocalDate> dates = expenditureDate.stream()
			.distinct()
			.collect(Collectors.toList());

		Collections.sort(dates, (d1, d2) -> d1.isAfter(d2) ? -1 : 1);

		int end = (int)offset + size > dates.size() ? dates.size() : (int)offset + size;

		return dates.subList(((int)offset), end);
	}

	private Long getDayIncomeAmount(LocalDate date) {
		Long amount = queryFactory.select(income.amount.sum())
			.from(income)
			.groupBy(income.registerDate.dayOfMonth())
			.where(income.registerDate.year().eq(date.getYear()),
				income.registerDate.month().eq(date.getMonthValue()),
				income.registerDate.dayOfMonth().eq(date.getDayOfMonth()))
			.fetchOne();

		return amount == null ? 0L : amount;
	}

	private Long getDayExpenditureAmount(LocalDate date) {
		Long amount = queryFactory.select(expenditure.amount.sum())
			.from(expenditure)
			.groupBy(expenditure.registerDate.dayOfMonth())
			.where(expenditure.registerDate.year().eq(date.getYear()),
				expenditure.registerDate.month().eq(date.getMonthValue()),
				expenditure.registerDate.dayOfMonth().eq(date.getDayOfMonth()))
			.fetchOne();

		return amount == null ? 0L : amount;
	}
}
