package com.prgrms.tenwonmoa.domain.accountbook.repository;

import static com.prgrms.tenwonmoa.domain.accountbook.QExpenditure.*;
import static com.prgrms.tenwonmoa.domain.accountbook.QIncome.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.dto.DayDetail;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindDayAccountResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindMonthAccountResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindSumResponse;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomImpl;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AccountBookQueryRepository {

	private final JPAQueryFactory queryFactory;

	/**
	 * 쿼리 개수를 최대 32개에서 고정 4개로 개선
	 * TODO : queryRepository 사이즈가 너무 커서 조금 나눠야할 필요성을 느끼고 있다...
	 * 향후 8.10 이후 고려
	 * Income과 Expenditure에 상관없이 재사용하고싶은 부분이 너무 많은데 어렵다...
	 * */
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
				List<DayDetail> dayDetails = monthExpenditure.stream()
					.filter(e -> e.getDate().isEqual(d))
					.map(
						e -> new DayDetail(e.getId(), CategoryType.EXPENDITURE.toString(), e.getAmount(),
							e.getContent(), e.getCategoryName(),
							e.getRegisterDate()))
					.collect(Collectors.toList());

				dayDetails.addAll(
					monthIncome.stream()
						.filter(i -> i.getDate().isEqual(d))
						.map(
							i -> new DayDetail(i.getId(), CategoryType.INCOME.toString(), i.getAmount(), i.getContent(),
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

	public FindMonthAccountResponse findMonthAccount(Long userId, int year) {
		return null;
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

		incomeSum = incomeSum == null ? 0L : incomeSum;
		expenditureSum = expenditureSum == null ? 0L : expenditureSum;
		return new FindSumResponse(incomeSum, expenditureSum);
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

}
