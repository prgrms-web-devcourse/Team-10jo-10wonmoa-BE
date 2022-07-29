package com.prgrms.tenwonmoa.domain.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.prgrms.tenwonmoa.domain.category.UserCategory;

@Repository
public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {
	@Override
	@Query("select uc from UserCategory uc "
		+ "join fetch uc.category "
		+ "join fetch uc.user where uc.id = :userCategoryId")
	Optional<UserCategory> findById(Long userCategoryId);

	List<UserCategory> findByUserId(Long userId);

}
