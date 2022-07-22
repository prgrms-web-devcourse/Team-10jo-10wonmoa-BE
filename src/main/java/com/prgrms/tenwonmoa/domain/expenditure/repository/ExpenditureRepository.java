package com.prgrms.tenwonmoa.domain.expenditure.repository;

import com.prgrms.tenwonmoa.domain.expenditure.Expenditure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

}
