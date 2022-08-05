package com.prgrms.tenwonmoa.domain.accountbook.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.accountbook.Income;

@Repository
public class AccountBookSearchRepository {

	@PersistenceContext
	private EntityManager em;

	public List<Expenditure> searchExpenditures(Long minPrice, Long maxPrice,
		LocalDateTime startDate, LocalDateTime endDate,
		String content, List<Long> userCategoryIds,
		int size, int page) {

		TypedQuery<Expenditure> query = em.createQuery("select e from Expenditure e "
			+ "where e.amount >= :minPrice "
			+ "and e.amount <= :maxPrice "
			+ "and e.registerDate >= :startDate "
			+ "and e.registerDate <= :endDate "
			+ "and e.userCategory.id in :userCategoryIds "
			+ "and e.content like CONCAT('%', :content, '%') "
			+ "order by e.registerDate desc", Expenditure.class);

		setParameters(query, Map.of(
			"minPrice", minPrice,
			"maxPrice", maxPrice,
			"startDate", startDate,
			"endDate", endDate,
			"userCategoryIds", userCategoryIds,
			"content", content));

		setPagingParam(query, size, page);

		return query.getResultList();
	}

	public List<Income> searchIncomes(Long minPrice, Long maxPrice,
		LocalDateTime startDate, LocalDateTime endDate,
		String content, List<Long> userCategoryIds,
		int size, int page) {

		TypedQuery<Income> query = em.createQuery("select i from Income i "
			+ "where i.amount >= :minPrice "
			+ "and i.amount <= :maxPrice "
			+ "and i.registerDate >= :startDate "
			+ "and i.registerDate <= :endDate "
			+ "and i.userCategory.id in :userCategoryIds "
			+ "and i.content like CONCAT('%', :content, '%') "
			+ "order by i.registerDate desc", Income.class);

		setParameters(query, Map.of(
			"minPrice", minPrice,
			"maxPrice", maxPrice,
			"startDate", startDate,
			"endDate", endDate,
			"userCategoryIds", userCategoryIds,
			"content", content));

		setPagingParam(query, size, page);

		return query.getResultList();
	}

	private void setParameters(TypedQuery query, Map<String, Object> params) {
		for (String key : params.keySet()) {
			query.setParameter(key, params.get(key));
		}
	}

	private void setPagingParam(TypedQuery query, int size, int page) {
		int offset = size * page;
		query.setFirstResult(offset);
		query.setMaxResults(size);
	}

}
