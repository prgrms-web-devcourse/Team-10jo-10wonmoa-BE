package com.prgrms.tenwonmoa.domain.accountbook.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.accountbook.Income;
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

	private void setParameters(TypedQuery<?> query, Map<String, Object> params) {
		for (String key : params.keySet()) {
			query.setParameter(key, params.get(key));
		}
	}

	private void setPagingParam(TypedQuery<?> query, PageCustomRequest pageRequest) {
		query.setFirstResult((int)pageRequest.getOffset());
		query.setMaxResults(pageRequest.getSize());
	}

}