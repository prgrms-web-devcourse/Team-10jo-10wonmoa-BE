package com.prgrms.tenwonmoa.domain.budget.repository;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static com.prgrms.tenwonmoa.domain.category.CategoryType.*;
import static org.assertj.core.api.Assertions.*;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.prgrms.tenwonmoa.common.fixture.RepositoryFixture;
import com.prgrms.tenwonmoa.domain.budget.Budget;
import com.prgrms.tenwonmoa.domain.budget.dto.FindBudgetData;
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
	private YearMonth now = YearMonth.now();

	@BeforeEach
	void init() {
		user = save(createUser());
		Category category = save(createExpenditureCategory());
		Category otherCategory = save(new Category("other", EXPENDITURE));
		userCategory1 = save(createUserCategory(user, category));
		userCategory2 = save(createUserCategory(user, otherCategory));

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

	@Test
	void 등록일로_조회_성공() {
		Category category3 = save(new Category("other3", EXPENDITURE));
		Category category4 = save(new Category("other4", EXPENDITURE));
		UserCategory userCategory3 = save(createUserCategory(user, category3));
		UserCategory userCategory4 = save(createUserCategory(user, category4));
		saveBudget(200L, now, user, userCategory2);
		saveBudget(300L, now, user, userCategory3);
		saveBudget(400L, now.plusMonths(1), user, userCategory4);

		List<Budget> findBudgets = budgetRepository.findByUserIdAndRegisterDate(user.getId(), now);
		List<Budget> findBudgets2 = budgetRepository.findByUserIdAndRegisterDate(user.getId(), now.plusMonths(1));
		List<Budget> findBudgets3 = budgetRepository.findByUserIdAndRegisterDate(user.getId(), now.plusMonths(2));

		assertThat(findBudgets).hasSize(3);
		assertThat(findBudgets2).hasSize(1);
		assertThat(findBudgets3).isEmpty();
	}

	@Test
	@Disabled
	void 엔플러스일_확인() {
		saveBudget(200L, now, user, userCategory2);

		List<Budget> findBudgets = budgetRepository.findByUserIdAndRegisterDate(user.getId(), now);
		log.info("=== findByRegisterDate의 Query를 지우면 of를 돌면서 쿼리가나간다. === ");
		findBudgets.forEach(
			(budget) -> {
				log.info("=== start === ");
				FindBudgetData.of(budget);
				log.info("=== end === ");
			}
		);
	}
}
