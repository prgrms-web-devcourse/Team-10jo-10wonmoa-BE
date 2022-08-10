package com.prgrms.tenwonmoa.domain.budget.repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.prgrms.tenwonmoa.domain.budget.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
	Optional<Budget> findByUserCategoryIdAndRegisterDate(Long userCategoryId, YearMonth registerDate);

	@Query("select b from Budget b join fetch b.userCategory where b.registerDate=:registerDate and b.user.id=:userId")
	List<Budget> findByUserIdAndRegisterDate(Long userId, YearMonth registerDate);
}
