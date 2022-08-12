package com.prgrms.tenwonmoa.domain.accountbook.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.prgrms.tenwonmoa.domain.accountbook.Income;

public interface IncomeRepository extends JpaRepository<Income, Long> {
	@Modifying(clearAutomatically = true)
	@Query("update Income i set i.userCategory = null "
		+ "where i.userCategory.id = :userCategoryId")
	void updateUserCategoryAsNull(Long userCategoryId);

	@Modifying(clearAutomatically = true)
	@Query("DELETE FROM Income i WHERE i.id = :id")
	void deleteById(Long id);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update Income i set i.categoryName = :categoryName "
		+ "where i.userCategory.id = :userCategoryId")
	void updateCategoryName(Long userCategoryId, String categoryName);

	@Query("select SUM(i.amount) from Income i "
		+ "where i.amount >= :minPrice "
		+ "and i.amount <= :maxPrice "
		+ "and i.registerDate >= :startDateTime "
		+ "and i.registerDate <= :endDateTime "
		+ "and i.content like CONCAT('%', :content, '%') "
		+ "and i.userCategory.id in :userCategoryIds "
		+ "and i.user.id = :userId")
	Long getSumOfIncome(Long minPrice, Long maxPrice,
		LocalDateTime startDateTime, LocalDateTime endDateTime,
		String content, List<Long> userCategoryIds, Long userId);

}
