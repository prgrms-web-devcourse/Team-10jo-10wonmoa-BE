package com.prgrms.tenwonmoa.domain.budget.repository;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static com.prgrms.tenwonmoa.domain.category.CategoryType.*;
import static org.assertj.core.api.Assertions.*;

import java.time.YearMonth;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.prgrms.tenwonmoa.common.fixture.RepositoryFixture;
import com.prgrms.tenwonmoa.domain.budget.Budget;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;

@DisplayName("예산 Repository 테스트")
class BudgetRepositoryTest extends RepositoryFixture {
	@Autowired
	private BudgetRepository budgetRepository;

	private UserCategory userCategory;
	private UserCategory otherUserCategory;
	private YearMonth now = YearMonth.now();

	@BeforeEach
	void init() {
		User user = save(createUser());
		Category category = save(createExpenditureCategory());
		Category otherCategory = save(new Category("other", EXPENDITURE));
		userCategory = save(createUserCategory(user, category));
		otherUserCategory = save(createUserCategory(user, otherCategory));
		budgetRepository.save(new Budget(100L, now, user, userCategory));
	}

	@Test
	void 유저카테고리이름_등록날짜로_조회성공() {
		Optional<Budget> findBudget = budgetRepository.findByUserCategoryIdAndRegisterDate(userCategory.getId(), now);

		assertThat(findBudget).isPresent();
		Budget budget = findBudget.get();
		assertThat(budget.getAmount()).isEqualTo(100L);
		assertThat(budget.getUserCategory()).isEqualTo(userCategory);
		assertThat(budget.getRegisterDate()).isEqualTo(now);
	}

	@Test
	void 유저카테고리이름_등록안된날짜로_조회() {
		Optional<Budget> findBudget = budgetRepository.findByUserCategoryIdAndRegisterDate(userCategory.getId(),
			now.plusMonths(1));

		assertThat(findBudget).isEmpty();
	}

	@Test
	void 유저카테고리이름_등록안된_카테고리로_조회() {
		Optional<Budget> findBudget = budgetRepository.findByUserCategoryIdAndRegisterDate(otherUserCategory.getId(),
			now);

		assertThat(findBudget).isEmpty();
	}
}
