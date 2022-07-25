package com.prgrms.tenwonmoa.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prgrms.tenwonmoa.domain.category.UserCategory;

@Repository
public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {

}
