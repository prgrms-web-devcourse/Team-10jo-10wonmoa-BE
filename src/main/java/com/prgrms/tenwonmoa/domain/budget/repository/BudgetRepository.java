package com.prgrms.tenwonmoa.domain.budget.repository;

import java.time.YearMonth;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.prgrms.tenwonmoa.domain.budget.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
	Optional<Budget> findByUserCategoryIdAndRegisterDate(Long userCategoryId, YearMonth registerDate);

	@Modifying
	@Query("delete from Budget b where b.user.id = :userId")
	void deleteAllByUserIdInQuery(Long userId);
}
