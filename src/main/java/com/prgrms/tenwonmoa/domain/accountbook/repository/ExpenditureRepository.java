package com.prgrms.tenwonmoa.domain.accountbook.repository;

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

	@Modifying
	@Query("delete from Expenditure e where e.user.id = :userId")
	void deleteAllByUserIdInQuery(Long userId);

}
