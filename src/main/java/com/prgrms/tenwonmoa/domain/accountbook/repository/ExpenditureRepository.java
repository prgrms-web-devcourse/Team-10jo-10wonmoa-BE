package com.prgrms.tenwonmoa.domain.accountbook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long>, ExpenditureCustomRepository {

	@Modifying(clearAutomatically = true)
	@Query("update Expenditure e set e.userCategory = null "
		+ "where e.userCategory.id = :userCategoryId")
	void updateUserCategoryAsNull(Long userCategoryId);

	@Override
	@Query("select e from Expenditure e "
		+ "left join fetch e.userCategory "
		+ "where e.id = :expenditureId")
	Optional<Expenditure> findById(Long expenditureId);
}
