package com.prgrms.tenwonmoa.domain.accountbook.repository;

import java.time.LocalDate;
import java.util.List;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;

public interface ExpenditureCustomRepository {

	List<Expenditure> findByRegisterDate(Long userId, LocalDate localDate);
}
