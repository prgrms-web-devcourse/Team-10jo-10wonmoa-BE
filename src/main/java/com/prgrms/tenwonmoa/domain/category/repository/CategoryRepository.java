package com.prgrms.tenwonmoa.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prgrms.tenwonmoa.domain.category.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}
