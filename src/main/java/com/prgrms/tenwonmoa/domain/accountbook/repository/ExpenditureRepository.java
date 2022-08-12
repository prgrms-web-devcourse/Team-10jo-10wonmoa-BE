package com.prgrms.tenwonmoa.domain.accountbook.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

	@Modifying(clearAutomatically = true)
	@Query("update Expenditure e set e.userCategory = null "
		+ "where e.userCategory.id = :userCategoryId")
	void updateUserCategoryAsNull(Long userCategoryId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update Expenditure e set e.categoryName = :categoryName "
		+ "where e.userCategory.id = :userCategoryId")
	void updateCategoryName(Long userCategoryId, String categoryName);

	@Query("select SUM(e.amount) from Expenditure e "
		+ "where e.amount >= :minPrice "
		+ "and e.amount <= :maxPrice "
		+ "and e.registerDate >= :startDateTime "
		+ "and e.registerDate <= :endDateTime "
		+ "and e.content like CONCAT('%', :content, '%') "
		+ "and e.userCategory.id in :userCategoryIds "
		+ "and e.user.id = :userId")
	Long getSumOfExpenditure(Long minPrice, Long maxPrice,
		LocalDateTime startDateTime, LocalDateTime endDateTime,
		String content, List<Long> userCategoryIds,
		Long userId);
}
