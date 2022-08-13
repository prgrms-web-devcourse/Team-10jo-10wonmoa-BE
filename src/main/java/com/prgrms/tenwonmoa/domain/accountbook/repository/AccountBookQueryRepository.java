package com.prgrms.tenwonmoa.domain.accountbook.repository;

import static com.prgrms.tenwonmoa.domain.accountbook.QExpenditure.*;
import static com.prgrms.tenwonmoa.domain.accountbook.QIncome.*;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.dto.AccountBookItem;
import com.prgrms.tenwonmoa.domain.accountbook.dto.DateDetail;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindCalendarResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindDayAccountResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindMonthAccountResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindSumResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.MonthCondition;
import com.prgrms.tenwonmoa.domain.accountbook.dto.MonthDetail;
import com.prgrms.tenwonmoa.domain.accountbook.dto.YearMonthCondition;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomImpl;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;
import com.prgrms.tenwonmoa.domain.common.page.PageResponse;
import com.prgrms.tenwonmoa.exception.message.Message;
import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class AccountBookQueryRepository {

	private final EntityManager em;
	private final JPAQueryFactory queryFactory;

	public AccountBookQueryRepository(EntityManager em, JPAQueryFactory queryFactory) {
		this.em = em;
		this.queryFactory = queryFactory;
	}

	// 일일 상세내역 pagination version 1
	public PageResponse<FindDayAccountResponse> findDailyAccount(Long userId, PageCustomRequest pageRequest,
		LocalDate date) {

		int year = date.getYear();
		int month = date.getMonthValue();

		// union으로 쿼리 2개
		List<LocalDate> totalDates = getPageTotalDate(pageRequest, userId, date);

		long total = totalDates.size();

		// 해당 월에 입력한 데이터 없으면 빈 데이터 내보내기
		if (total == 0) {
			return new PageCustomImpl<>(pageRequest, 0, Collections.EMPTY_LIST);
		}

		int offset = (int)pageRequest.getOffset();
		int end = (int)Math.min(offset + pageRequest.getSize(), total);

		List<LocalDate> dates = totalDates.subList(offset, end);

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

		return new PageCustomImpl<>(pageRequest, total, responses);
	}

	// 일일 상세내역 pagination version 2
	public PageResponse<FindDayAccountResponse> findDailyAccountVer2(Long userId,
		PageCustomRequest pageRequest,
		YearMonthCondition condition) {
		int year = condition.getYear();
		int month = condition.getMonth();

		// union 후 page date 다 가져오기.
		String unionQuery =
			"SELECT * FROM "
				+ "(SELECT e.id, c.category_kind, e.amount, e.content, e.category_name, "
				+ "e.register_date, uc.id as user_category_id, e.user_id FROM expenditure e "
				+ "LEFT JOIN user_category uc on e.user_category_id = uc.id "
				+ "JOIN category c on uc.category_id=c.id "
				+ "UNION "
				+ "SELECT i.id, c.category_kind, i.amount, i.content, i.category_name, "
				+ "i.register_date, uc.id as user_category_id, i.user_id from income i "
				+ "LEFT JOIN user_category uc on i.user_category_id =uc.id "
				+ "JOIN category c on uc.category_id=c.id) total "
				+ "WHERE user_id = ? and "
				+ "YEAR(register_date) = ? and "
				+ "MONTH(register_date) = ? "
				+ "ORDER BY register_date desc";

		String countQuery = "SELECT COUNT(*) FROM "
			+ "(SELECT e.id, c.category_kind, e.amount, e.content, e.category_name, "
			+ "e.register_date, uc.id as user_category_id, e.user_id FROM expenditure e "
			+ "LEFT JOIN user_category uc on e.user_category_id = uc.id "
			+ "JOIN category c on uc.category_id=c.id "
			+ "UNION "
			+ "SELECT i.id, c.category_kind, i.amount, i.content, i.category_name, "
			+ "i.register_date, uc.id as user_category_id, i.user_id from income i "
			+ "LEFT JOIN user_category uc on i.user_category_id = uc.id "
			+ "JOIN category c on uc.category_id=c.id) total "
			+ "WHERE total.user_id = ? and "
			+ "YEAR (total.register_date) = ? and "
			+ "MONTH(total.register_date) = ?";

		Query nativeCountQuery = em.createNativeQuery(countQuery);
		setParameters(nativeCountQuery, userId, year, month);
		Object countResult = nativeCountQuery.getSingleResult();
		long totalElements = ((BigInteger)countResult).longValue();

		// 해당 월에 입력한 데이터 없으면 빈 데이터 내보내기
		if (totalElements == 0) {
			return new PageCustomImpl<>(pageRequest, totalElements, Collections.EMPTY_LIST);
		}

		// 요청한 페이지 없을 경우
		if (totalElements < pageRequest.getOffset()) {
			throw new NoSuchElementException(Message.INVALID_PAGE_NUMBER.getMessage());
		}

		// paging 처리후 AccountBookItem으로 binding
		Query nativeUnionQuery = em.createNativeQuery(unionQuery);
		setParameters(nativeUnionQuery, userId, year, month);
		setPagingParam(nativeUnionQuery, pageRequest);

		List<Object[]> resultObjects = nativeUnionQuery.getResultList();

		List<AccountBookItem> accountBookItems = resultObjects.stream()
			.map(this::bindData)
			.collect(Collectors.toList());

		// 그리고 수입과 지출이 작성된 날짜, 날짜 중복 X
		List<LocalDate> registerDates = getRegisterDates(accountBookItems);

		LocalDate startDate = registerDates.get(registerDates.size() - 1);
		LocalDate endDate = registerDates.get(0);

		// 일일 지출과 수입 합계
		Map<Integer, Long> expenditureSumMap = getDayExpenditureSum(userId, year, month, startDate, endDate);
		Map<Integer, Long> incomeSumMap = getDayIncomeSumMap(userId, year, month, startDate, endDate);

		List<FindDayAccountResponse> responses = getFindDayAccountResponses(
			accountBookItems, registerDates, expenditureSumMap, incomeSumMap);

		return new PageCustomImpl<>(pageRequest, totalElements, responses);
	}

	private List<FindDayAccountResponse> getFindDayAccountResponses(List<AccountBookItem> accountBookItems,
		List<LocalDate> registerDates, Map<Integer, Long> expenditureSumMap, Map<Integer, Long> incomeSumMap) {
		List<FindDayAccountResponse> responses = new ArrayList<>();

		registerDates.iterator().forEachRemaining(
			d -> {
				List<AccountBookItem> accounts = accountBookItems.stream().filter(
					accountBookItem -> accountBookItem.getRegisterTime().toLocalDate().isEqual(d)
				).collect(Collectors.toList());

				responses.add(
					new FindDayAccountResponse(d, getAmountZeroIfNull(incomeSumMap.get(d)), getAmountZeroIfNull(
						expenditureSumMap.get(d)), accounts));
			}
		);
		return responses;
	}

	private Map<Integer, Long> getDayIncomeSumMap(Long userId, int year, int month, LocalDate startDate,
		LocalDate endDate) {
		return queryFactory.from(income)
			.where(
				income.registerDate.year().eq(year),
				income.user.id.eq(userId),
				income.registerDate.month().eq(month),
				income.registerDate.between(startDate.atTime(LocalTime.MIN), endDate.atTime(LocalTime.MAX))
			)
			.transform(GroupBy.groupBy(income.registerDate.dayOfMonth())
				.as(GroupBy.sum(income.amount))
			);
	}

	private Map<Integer, Long> getDayExpenditureSum(Long userId, int year, int month, LocalDate startDate,
		LocalDate endDate) {
		return queryFactory.from(expenditure)
			.where(
				expenditure.user.id.eq(userId),
				expenditure.registerDate.year().eq(year),
				expenditure.registerDate.month().eq(month),
				expenditure.registerDate.between(startDate.atTime(LocalTime.MIN), endDate.atTime(LocalTime.MAX))
			)
			.transform(GroupBy.groupBy(expenditure.registerDate.dayOfMonth())
				.as(GroupBy.sum(expenditure.amount))
			);
	}

	private List<LocalDate> getRegisterDates(List<AccountBookItem> accountBookItems) {
		return accountBookItems.stream()
			.map(accountBookItem -> accountBookItem.getRegisterTime().toLocalDate())
			.distinct()
			.sorted((d1, d2) -> d1.isAfter(d2) ? -1 : 1)
			.collect(Collectors.toList());
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

	public FindCalendarResponse findCalendarAccount(Long userId, YearMonthCondition condition) {

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
			results.add(new DateDetail(LocalDate.of(year, month, day), incomeSum, expenditureSum));
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

	private List<LocalDate> getPageTotalDate(PageCustomRequest pageCustomRequest, Long userId, LocalDate date) {

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

		return dates;
	}

	private Long getAmountZeroIfNull(Long amount) {
		return amount == null ? 0 : amount;
	}

	private AccountBookItem bindData(Object[] objects) {
		long id = ((BigInteger)objects[0]).longValue();
		String categoryKind = (String)objects[1];
		long amount = ((BigInteger)objects[2]).longValue();
		String content = (String)objects[3];
		String categoryName = (String)objects[4];
		LocalDateTime registerTime = ((Timestamp)objects[5]).toLocalDateTime();

		return new AccountBookItem(id, categoryKind, amount, content, categoryName, registerTime);
	}

	private void setParameters(Query query, Object... params) {
		for (int i = 1; i <= params.length; i++) {
			query.setParameter(i, params[i - 1]);
		}
	}

	private void setPagingParam(Query query, PageCustomRequest pageRequest) {
		query.setFirstResult((int)pageRequest.getOffset());
		query.setMaxResults(pageRequest.getSize());
	}
}


