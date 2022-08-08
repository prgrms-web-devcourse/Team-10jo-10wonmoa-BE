package com.prgrms.tenwonmoa.domain.accountbook.repository;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.dto.AccountBookItem;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;

@Repository
public class SearchAccountBookRepository {

	@PersistenceContext
	private EntityManager em;

	public List<Expenditure> searchExpenditures(Long minPrice, Long maxPrice,
		LocalDate startDate, LocalDate endDate,
		String content, List<Long> userCategoryIds,
		Long userId,
		PageCustomRequest pageRequest) {

		TypedQuery<Expenditure> query = em.createQuery("select e from Expenditure e "
			+ "join fetch e.userCategory "
			+ "where e.amount >= :minPrice "
			+ "and e.amount <= :maxPrice "
			+ "and e.registerDate >= :startDate "
			+ "and e.registerDate <= :endDate "
			+ "and e.userCategory.id in :userCategoryIds "
			+ "and e.content like CONCAT('%', :content, '%') "
			+ "and e.user.id = :userId "
			+ "order by e.registerDate desc", Expenditure.class);

		setParameters(query, Map.of(
			"minPrice", minPrice,
			"maxPrice", maxPrice,
			"startDate", startDate.atTime(LocalTime.MIN),
			"endDate", endDate.atTime(LocalTime.MAX),
			"userCategoryIds", userCategoryIds,
			"userId", userId,
			"content", content));

		setPagingParam(query, pageRequest);

		return query.getResultList();
	}

	public List<Income> searchIncomes(Long minPrice, Long maxPrice,
		LocalDate startDate, LocalDate endDate,
		String content, List<Long> userCategoryIds,
		Long userId, PageCustomRequest pageRequest) {

		TypedQuery<Income> query = em.createQuery("select i from Income i "
			+ "join fetch i.userCategory "
			+ "where i.amount >= :minPrice "
			+ "and i.amount <= :maxPrice "
			+ "and i.registerDate >= :startDate "
			+ "and i.registerDate <= :endDate "
			+ "and i.userCategory.id in :userCategoryIds "
			+ "and i.content like CONCAT('%', :content, '%') "
			+ "and i.user.id = :userId "
			+ "order by i.registerDate desc", Income.class);

		setParameters(query, Map.of(
			"minPrice", minPrice,
			"maxPrice", maxPrice,
			"startDate", startDate.atTime(LocalTime.MIN),
			"endDate", endDate.atTime(LocalTime.MAX),
			"userCategoryIds", userCategoryIds,
			"userId", userId,
			"content", content));

		setPagingParam(query, pageRequest);

		return query.getResultList();
	}

	public List<AccountBookItem> searchAccountBook(Long minPrice, Long maxPrice,
		LocalDate startDate, LocalDate endDate,
		String content, List<Long> userCategoryIds,
		Long userId, PageCustomRequest pageRequest) {

		String unionQuery =
			"SELECT * FROM "
				+ "(SELECT e.id, c.category_kind, e.amount, e.content, e.category_name, "
				+ "e.register_date, uc.id as user_category_id, e.user_id FROM expenditure e "
				+ "LEFT JOIN user_category uc on e.user_category_id=uc.id "
				+ "JOIN category c on uc.category_id=c.id "
				+ "UNION "
				+ "SELECT i.id, c.category_kind, i.amount, i.content, i.category_name, "
				+ "i.register_date, uc.id as user_category_id, i.user_id from income i "
				+ "LEFT JOIN user_category uc on i.user_category_id=uc.id "
				+ "JOIN category c on uc.category_id=c.id) total "
				+ "WHERE user_id = ? and "
				+ "content like CONCAT('%', ?, '%') and "
				+ "? <= amount and amount <= ? and "
				+ "? <= register_date and register_date <= ? and "
				+ userCategoryWhere(userCategoryIds)
				+ " ORDER BY register_date desc";

		Query nativeQuery = em.createNativeQuery(unionQuery);
		setParameters(nativeQuery, userCategoryIds, userId, content, minPrice, maxPrice, startDate, endDate);
		setPagingParam(nativeQuery, pageRequest);

		List<Object[]> results = nativeQuery.getResultList();

		return results.stream()
			.map(this::bindData)
			.collect(Collectors.toList());
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

	private String userCategoryWhere(List<Long> userCategoryIds) {
		if (userCategoryIds.isEmpty()) {
			return "";
		}
		String[] params = new String[userCategoryIds.size()];
		Arrays.fill(params, "?");
		return "user_category_id in (" + String.join(",", params) + ") ";
	}

	private void setParameters(Query query, List<Long> userCategoryIds, Object... params) {
		int idx;

		for (idx = 1; idx <= params.length; idx++) {
			query.setParameter(idx, params[idx - 1]);
		}
		for (Long userCategoryId : userCategoryIds) {
			query.setParameter(idx++, userCategoryId);
		}
	}

	private void setParameters(TypedQuery<?> query, Map<String, Object> params) {
		for (String key : params.keySet()) {
			query.setParameter(key, params.get(key));
		}
	}

	private void setPagingParam(Query query, PageCustomRequest pageRequest) {
		query.setFirstResult((int)pageRequest.getOffset());
		query.setMaxResults(pageRequest.getSize());
	}

}
