package com.prgrms.tenwonmoa.domain.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.tenwonmoa.domain.budget.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
}
