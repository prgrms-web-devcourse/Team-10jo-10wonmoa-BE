package com.prgrms.tenwonmoa.domain.category.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.prgrms.tenwonmoa.domain.category.UserCategory;

@Repository
public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {

	@Query(value = "select uc from UserCategory uc"
		+ " join fetch uc.category"
		+ " where uc.user.id = :userId"
		+ " and uc.category.id =:categoryId")
	Optional<UserCategory> findByUserAndCategory(Long userId, Long categoryId);

	@Override
	@Query("select uc from UserCategory uc "
		+ "join fetch uc.category "
		+ "join fetch uc.user where uc.id = :userCategoryId")
	Optional<UserCategory> findById(Long userCategoryId);

}
