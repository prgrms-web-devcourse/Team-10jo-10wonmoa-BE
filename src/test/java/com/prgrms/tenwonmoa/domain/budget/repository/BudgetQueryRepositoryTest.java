package com.prgrms.tenwonmoa.domain.budget.repository;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static com.prgrms.tenwonmoa.domain.category.CategoryType.*;
import static org.assertj.core.api.Assertions.*;

import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.prgrms.tenwonmoa.common.fixture.RepositoryFixture;
import com.prgrms.tenwonmoa.domain.budget.dto.FindBudgetData;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;

class BudgetQueryRepositoryTest extends RepositoryFixture {
	private User user;
	private User otherUser;
	private YearMonth now = YearMonth.of(2022, 1);

	// user -> category1
	private UserCategory uc1;
	// user -> category2
	private UserCategory uc2;
	// user -> category3
	private UserCategory uc3;
	// otherUser -> category4
	private UserCategory uc4;
	// otherUser -> category5
	private UserCategory uc5;

	@Autowired
	private BudgetQueryRepository budgetQueryRepository;

	@BeforeEach
	void init() {
		user = saveRandomUser();
		otherUser = saveRandomUser();
		initBudgetData();
	}

	@Test
	void 유저의카테고리별_예산금액_조회_성공() {
		// given
		saveBudget(200L, now, user, uc1);
		saveBudget(100L, now, user, uc2);
		// when
		List<FindBudgetData> findBudgetData = budgetQueryRepository.searchUserCategoriesWithBudget(user.getId(), now);

		assertThat(findBudgetData).hasSize(3);
		assertThat(findBudgetData).extracting("amount").containsExactly(200L, 100L, 0L);
	}

	@Test
	void 등록된_예산이_없는경우() {
		// when
		List<FindBudgetData> findBudgetData = budgetQueryRepository.searchUserCategoriesWithBudget(otherUser.getId(),
			now);

		assertThat(findBudgetData).hasSize(2);
		assertThat(findBudgetData).extracting("amount").containsExactly(0L, 0L);
	}

	@Test
	void 데이터가없는_날짜로_요청하는_경우() {
		// given
		saveBudget(200L, now, user, uc1);
		saveBudget(100L, now, user, uc2);
		// when
		List<FindBudgetData> findBudgetData = budgetQueryRepository.searchUserCategoriesWithBudget(
			otherUser.getId(),
			now.plusMonths(1));

		assertThat(findBudgetData).hasSize(2);
		assertThat(findBudgetData).extracting("amount").containsExactly(0L, 0L);
	}

	@Test
	void 여러_유저의_예산이등록된_경우_잘찾아오는지_테스트() {
		// given
		saveBudget(200L, now, user, uc3);

		saveBudget(10L, now, otherUser, uc4);
		saveBudget(20L, now, otherUser, uc5);
		// when
		List<FindBudgetData> budgets1 = budgetQueryRepository.searchUserCategoriesWithBudget(user.getId(), now);
		List<FindBudgetData> budgets2 = budgetQueryRepository.searchUserCategoriesWithBudget(otherUser.getId(), now);

		assertThat(budgets1).hasSize(3);
		assertThat(budgets1).extracting("amount").containsExactly(200L, 0L, 0L);
		assertThat(budgets2).hasSize(2);
		assertThat(budgets2).extracting("amount").containsExactly(20L, 10L);
	}

	private void initBudgetData() {
		Category category1 = save(new Category("ct1", EXPENDITURE));
		Category category2 = save(new Category("ct2", EXPENDITURE));
		Category category3 = save(new Category("ct3", EXPENDITURE));
		Category category4 = save(new Category("ct4", EXPENDITURE));
		Category category5 = save(new Category("ct5", EXPENDITURE));

		uc1 = save(createUserCategory(user, category1));
		uc2 = save(createUserCategory(user, category2));
		uc3 = save(createUserCategory(user, category3));
		uc4 = save(createUserCategory(otherUser, category4));
		uc5 = save(createUserCategory(otherUser, category5));
	}

}