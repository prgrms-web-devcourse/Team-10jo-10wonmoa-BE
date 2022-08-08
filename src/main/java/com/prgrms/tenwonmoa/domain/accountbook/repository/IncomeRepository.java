package com.prgrms.tenwonmoa.domain.accountbook.repository;

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
}
