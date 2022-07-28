package com.prgrms.tenwonmoa.domain.accountbook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.tenwonmoa.domain.accountbook.Income;

public interface IncomeRepository extends JpaRepository<Income, Long> {
	Optional<Income> findByIdAndUserId(Long incomeId, Long userId);
}
