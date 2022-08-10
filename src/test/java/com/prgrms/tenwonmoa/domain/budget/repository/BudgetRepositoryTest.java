package com.prgrms.tenwonmoa.domain.budget.repository;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static com.prgrms.tenwonmoa.domain.category.CategoryType.*;
import static org.assertj.core.api.Assertions.*;

import java.time.YearMonth;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.prgrms.tenwonmoa.common.fixture.RepositoryFixture;
import com.prgrms.tenwonmoa.domain.budget.Budget;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;

import lombok.extern.slf4j.Slf4j;

@DisplayName("예산 Repository 테스트")
@Slf4j
class BudgetRepositoryTest extends RepositoryFixture {
	@Autowired
	private BudgetRepository budgetRepository;

	private UserCategory userCategory1;
	private UserCategory userCategory2;
	private User user;
	private YearMonth now = YearMonth.of(2020, 01);

	@Autowired
	EntityManager em;

	@BeforeEach
	void init() {
		user = save(createUser());
		Category category = save(new Category("category1", EXPENDITURE));
		Category category2 = save(new Category("category2", EXPENDITURE));
		userCategory1 = save(createUserCategory(user, category));
		userCategory2 = save(createUserCategory(user, category2));

		saveBudget(100L, now, user, userCategory1);
	}

	@Test
	void 유저카테고리이름_등록날짜로_조회성공() {
		Optional<Budget> findBudget = budgetRepository.findByUserCategoryIdAndRegisterDate(userCategory1.getId(), now);

		assertThat(findBudget).isPresent();
		Budget budget = findBudget.get();
		assertThat(budget.getAmount()).isEqualTo(100L);
		assertThat(budget.getUserCategory()).isEqualTo(userCategory1);
		assertThat(budget.getRegisterDate()).isEqualTo(now);
	}

	@Test
	void 유저카테고리이름_등록안된날짜로_조회() {
		Optional<Budget> findBudget = budgetRepository.findByUserCategoryIdAndRegisterDate(userCategory1.getId(),
			now.plusMonths(1));

		assertThat(findBudget).isEmpty();
	}

	@Test
	void 유저카테고리이름_등록안된_카테고리로_조회() {
		Optional<Budget> findBudget = budgetRepository.findByUserCategoryIdAndRegisterDate(userCategory2.getId(),
			now);

		assertThat(findBudget).isEmpty();
	}
}
