package com.prgrms.tenwonmoa.domain.expenditure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.tenwonmoa.domain.expenditure.Expenditure;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

}
