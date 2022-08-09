package com.prgrms.tenwonmoa.domain.budget.repository;

import java.time.YearMonth;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.tenwonmoa.domain.budget.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
	Optional<Budget> findByUserCategoryIdAndRegisterDate(Long userCategoryId, YearMonth registerDate);
}
