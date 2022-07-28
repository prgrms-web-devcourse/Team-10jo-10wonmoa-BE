package com.prgrms.tenwonmoa.domain.accountbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long>, ExpenditureCustomRepository {

}
