package com.prgrms.tenwonmoa.domain.accountbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.prgrms.tenwonmoa.domain.accountbook.Income;

public interface IncomeRepository extends JpaRepository<Income, Long> {

	@Modifying(clearAutomatically = true)
	@Query("update Income i set i.userCategory = null where i.userCategory.id = :userCategoryId")
	void updateUserCategoryAsNull(Long userCategoryId);

}
