package com.prgrms.tenwonmoa.domain.accountbook.repository;

import static com.prgrms.tenwonmoa.domain.accountbook.QExpenditure.*;
import static com.prgrms.tenwonmoa.domain.accountbook.QIncome.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.dto.AccountBookItem;
import com.prgrms.tenwonmoa.domain.accountbook.dto.CalendarCondition;
import com.prgrms.tenwonmoa.domain.accountbook.dto.DateDetail;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindCalendarResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindDayAccountResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindMonthAccountResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindSumResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.MonthCondition;
import com.prgrms.tenwonmoa.domain.accountbook.dto.MonthDetail;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomImpl;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;
import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AccountBookQueryRepository {

	private final JPAQueryFactory queryFactory;

	// 일일 상세내역 pagination version 1
	public PageCustomImpl<FindDayAccountResponse> findDailyAccount(Long userId, PageCustomRequest pageRequest,
		LocalDate date) {

		int year = date.getYear();
		int month = date.getMonthValue();

		// union으로 쿼리 2개
		List<LocalDate> dates = getPageDate(pageRequest, userId, date);

		// 해당 월에 입력한 데이터 없으면 빈 데이터 내보내기
		if (dates.size() == 0) {
			return new PageCustomImpl<>(pageRequest, Collections.EMPTY_LIST);
		}

		LocalDate firstDate = dates.get(0);
		LocalDate lastDate = dates.get(dates.size() - 1);

		// Expenditure 쿼리 1개
		List<Expenditure> monthExpenditure = queryFactory.select(expenditure)
			.from(expenditure)
			.where(expenditure.user.id.eq(userId),
				expenditure.registerDate.year().eq(year),
				expenditure.registerDate.month().eq(month),
				expenditure.registerDate.between(lastDate.atTime(0, 0, 00), firstDate.atTime(23, 59, 59))
			)
			.fetch();

		// Income 쿼리 1개 : 여기까지 총 네 개입니다.
		List<Income> monthIncome = queryFactory.select(income)
			.from(income)
			.where(income.user.id.eq(userId),
				income.registerDate.year().eq(year),
				income.registerDate.month().eq(month),
				income.registerDate.between(lastDate.atTime(0, 0, 00), firstDate.atTime(23, 59, 59))
			)
			.fetch();

		// Map과 Collection 사용하여 날짜별 수입 지출에 대한 groupBy를 코드에서 해결
		Map<LocalDate, Long> dayExpenditureSum = monthExpenditure.stream()
			.collect(Collectors.groupingBy(Expenditure::getDate, Collectors.summingLong(Expenditure::getAmount)));

		Map<LocalDate, Long> dayIncomeSum = monthIncome.stream()
			.collect(Collectors.groupingBy(Income::getDate, Collectors.summingLong(Income::getAmount)));

		List<FindDayAccountResponse> responses = new ArrayList<>();

		// pageDate 가져 온 것을 iterator 돌려서 날짜에 해단하는 response 만들기.
		dates.iterator().forEachRemaining(
			d -> {
				List<AccountBookItem> dayDetails = monthExpenditure.stream()
					.filter(e -> e.getDate().isEqual(d))
					.map(
						e -> new AccountBookItem(e.getId(), CategoryType.EXPENDITURE.toString(), e.getAmount(),
							e.getContent(), e.getCategoryName(),
							e.getRegisterDate()))
					.collect(Collectors.toList());

				dayDetails.addAll(
					monthIncome.stream()
						.filter(i -> i.getDate().isEqual(d))
						.map(
							i -> new AccountBookItem(i.getId(), CategoryType.INCOME.toString(), i.getAmount(),
								i.getContent(),
								i.getCategoryName(), i.getRegisterDate()))
						.collect(Collectors.toList())
				);

				Collections.sort(dayDetails);

				responses.add(new FindDayAccountResponse(d, dayIncomeSum.getOrDefault(d, 0L),
					dayExpenditureSum.getOrDefault(d, 0L), dayDetails));
			}
		);

		Collections.sort(responses);

		return new PageCustomImpl<>(pageRequest, responses);
	}

	public FindSumResponse findMonthSum(Long userId, LocalDate month) {

		Long incomeSum = queryFactory.select(income.amount.sum())
			.from(income)
			.groupBy(income.registerDate.yearMonth())
			.where(income.registerDate.year().eq(month.getYear()),
				income.registerDate.month().eq(month.getMonthValue()),
				income.user.id.eq(userId)
			)
			.fetchOne();

		Long expenditureSum = queryFactory.select(expenditure.amount.sum())
			.from(expenditure)
			.groupBy(expenditure.registerDate.yearMonth())
			.where(expenditure.registerDate.year().eq(month.getYear()),
				expenditure.registerDate.month().eq(month.getMonthValue()),
				expenditure.user.id.eq(userId)
			)
			.fetchOne();

		incomeSum = incomeSum == null ? 0L : incomeSum;
		expenditureSum = expenditureSum == null ? 0L : expenditureSum;
		return new FindSumResponse(incomeSum, expenditureSum);
	}

	public FindMonthAccountResponse findMonthAccount(Long userId, MonthCondition condition) {

		int year = condition.getConditionYear();

		Map<Integer, Long> expenditureMonthMap = queryFactory.from(expenditure)
			.where(
				expenditure.user.id.eq(userId),
				expenditure.registerDate.year().eq(year)
			)
			.transform(GroupBy.groupBy(expenditure.registerDate.month())
				.as(GroupBy.sum(expenditure.amount))
			);

		Map<Integer, Long> incomeMonthMap = queryFactory.from(income)
			.where(
				income.registerDate.year().eq(year),
				income.user.id.eq(userId)
			)
			.transform(GroupBy.groupBy(income.registerDate.month())
				.as(GroupBy.sum(income.amount))
			);

		List<Integer> monthList = new ArrayList<>(expenditureMonthMap.keySet());
		monthList.addAll(incomeMonthMap.keySet());

		List<MonthDetail> results = getMonthAccountResults(condition, new HashSet<>(monthList), expenditureMonthMap,
			incomeMonthMap);

		return new FindMonthAccountResponse(results);
	}

	public FindSumResponse findYearSum(Long userId, int year) {

		Long incomeSum = queryFactory.select(income.amount.sum())
			.from(income)
			.groupBy(income.registerDate.year())
			.where(income.registerDate.year().eq(year),
				income.user.id.eq(userId)
			)
			.fetchOne();

		Long expenditureSum = queryFactory.select(expenditure.amount.sum())
			.from(expenditure)
			.groupBy(expenditure.registerDate.year())
			.where(expenditure.registerDate.year().eq(year),
				expenditure.user.id.eq(userId)
			)
			.fetchOne();

		incomeSum = getAmountZeroIfNull(incomeSum);
		expenditureSum = getAmountZeroIfNull(expenditureSum);
		return new FindSumResponse(incomeSum, expenditureSum);
	}

	public FindCalendarResponse findCalendarAccount(Long userId, CalendarCondition condition) {

		int year = condition.getYear();
		int month = condition.getMonth();
		int lastDay = condition.getLastDayOfMonth();

		Map<Integer, Long> incomeDateMap = queryFactory.from(income)
			.where(
				income.registerDate.year().eq(year),
				income.registerDate.month().eq(month),
				income.user.id.eq(userId)
			)
			.transform(GroupBy.groupBy(income.registerDate.dayOfMonth())
				.as(GroupBy.sum(income.amount))
			);

		Map<Integer, Long> expenditureDateMap = queryFactory.from(expenditure)
			.where(
				expenditure.user.id.eq(userId),
				expenditure.registerDate.year().eq(year),
				expenditure.registerDate.month().eq(month)
			)
			.transform(GroupBy.groupBy(expenditure.registerDate.dayOfMonth())
				.as(GroupBy.sum(expenditure.amount))
			);

		List<DateDetail> results = new ArrayList<>();

		for (int day = 1; day <= lastDay; day++) {
			Long incomeSum = getAmountZeroIfNull(incomeDateMap.get(day));
			Long expenditureSum = getAmountZeroIfNull(expenditureDateMap.get(day));
			results.add(new DateDetail(day, incomeSum, expenditureSum));
		}

		return new FindCalendarResponse(month, results);
	}

	private List<MonthDetail> getMonthAccountResults(MonthCondition monthCondition, Set<Integer> monthSet,
		Map<Integer, Long> expenditureMap, Map<Integer, Long> incomeMap) {

		boolean isFuture = monthCondition.isFuture();
		if (isFuture) {
			return getMonthDetails(monthSet, expenditureMap, incomeMap);
		}

		Set<Integer> notFutureMonthSet = monthCondition.getNotFutureMonthSet(monthSet);

		return getMonthDetails(notFutureMonthSet, expenditureMap, incomeMap);
	}

	private List<MonthDetail> getMonthDetails(Set<Integer> monthSet, Map<Integer, Long> expenditureMap,
		Map<Integer, Long> incomeMap) {

		List<MonthDetail> results = new ArrayList<>();
		Iterator<Integer> iter = monthSet.iterator();
		while (iter.hasNext()) {
			int month = iter.next();
			Long incomeSum = getAmountZeroIfNull(incomeMap.get(month));
			Long expenditureSum = getAmountZeroIfNull(expenditureMap.get(month));
			results.add(new MonthDetail(incomeSum, expenditureSum, month));
		}
		return results.stream()
			.sorted((d1, d2) -> d1.getMonth() > d2.getMonth() ? -1 : 1)
			.collect(Collectors.toList());
	}

	private List<LocalDate> getPageDate(PageCustomRequest pageCustomRequest, Long userId, LocalDate date) {

		long offset = pageCustomRequest.getOffset();
		int size = pageCustomRequest.getSize();
		int year = date.getYear();
		int month = date.getMonthValue();

		List<LocalDateTime> dateTimes = queryFactory.select(expenditure.registerDate)
			.from(expenditure)
			.where(
				expenditure.user.id.eq(userId),
				expenditure.registerDate.year().eq(year),
				expenditure.registerDate.month().eq(month)
			)
			.fetch();

		dateTimes.addAll(
			queryFactory.select(income.registerDate)
				.from(income)
				.where(
					income.user.id.eq(userId),
					income.registerDate.year().eq(year),
					income.registerDate.month().eq(month)
				)
				.fetch()
		);

		List<LocalDate> dates = dateTimes.stream()
			.map(LocalDateTime::toLocalDate)
			.distinct()
			.sorted((d1, d2) -> d1.isAfter(d2) ? -1 : 1)
			.collect(Collectors.toList());

		int end = Math.min((int)offset + size, dates.size());

		return dates.subList(((int)offset), end);
	}

	private Long getAmountZeroIfNull(Long amount) {
		return amount == null ? 0 : amount;
	}
}


