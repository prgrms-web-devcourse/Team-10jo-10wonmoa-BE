package com.prgrms.tenwonmoa.domain.category.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.prgrms.tenwonmoa.domain.category.UserCategory;

@Repository
public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {

	@Query(value = "select u from UserCategory u"
		+ " where u.user.id = :userId"
		+ " and u.category.id =:categoryId")
	Optional<UserCategory> findByUserAndCategory(Long userId, Long categoryId);

}
