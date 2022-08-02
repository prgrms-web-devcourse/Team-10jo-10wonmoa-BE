package com.prgrms.tenwonmoa.domain.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;

@Repository
public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {

	@Query("select uc from UserCategory uc "
		+ "join fetch uc.category c "
		+ "where uc.user.id = :userId "
		+ "and c.categoryType = :categoryType")
	List<UserCategory> findByUserIdAndCategoryType(Long userId, CategoryType categoryType);
}
