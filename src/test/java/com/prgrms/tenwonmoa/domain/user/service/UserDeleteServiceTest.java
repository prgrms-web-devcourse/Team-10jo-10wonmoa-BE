package com.prgrms.tenwonmoa.domain.user.service;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.prgrms.tenwonmoa.common.RepositoryTest;
import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.repository.ExpenditureRepository;
import com.prgrms.tenwonmoa.domain.accountbook.repository.IncomeRepository;
import com.prgrms.tenwonmoa.domain.budget.Budget;
import com.prgrms.tenwonmoa.domain.budget.repository.BudgetRepository;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;

@DisplayName("UserDeleteService 테스트")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Import(UserDeleteService.class)
class UserDeleteServiceTest extends RepositoryTest {

	@Autowired
	private IncomeRepository incomeRepository;

	@Autowired
	private ExpenditureRepository expenditureRepository;

	@Autowired
	private BudgetRepository budgetRepository;

	@Autowired
	private UserCategoryRepository userCategoryRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserDeleteService userDeleteService;

	private User user;

	private Category incomeCategory;
	private Category expenditureCategory;

	private UserCategory userCategory;

	@BeforeEach
	void setup() {
		user = save(createUser());
		incomeCategory = save(createIncomeCategory());
		expenditureCategory = save(createExpenditureCategory());
		userCategory = save(new UserCategory(user, incomeCategory));
		userCategory = save(new UserCategory(user, expenditureCategory));
	}

	@Test
	void 데이터와_유저_삭제() {
		// given
		createIncomes(10, user, userCategory);
		createExpenditures(20, user, userCategory);
		createBudgets(5, user, userCategory);

		// when
		userDeleteService.deleteUserData(user.getId());

		List<Income> incomes = incomeRepository.findAll();
		List<Expenditure> expenditures = expenditureRepository.findAll();
		List<UserCategory> userCategories = userCategoryRepository.findAll();
		List<Budget> budgets = budgetRepository.findAll();
		List<User> users = userRepository.findAll();

		// then
		assertAll(
			() -> assertThat(incomes).hasSize(0),
			() -> assertThat(expenditures).hasSize(0),
			() -> assertThat(userCategories).hasSize(0),
			() -> assertThat(budgets).hasSize(0),
			() -> assertThat(users).hasSize(0)
		);
	}

	@Test
	void 데이터가_없는_유저_삭제() {
		// given when
		userDeleteService.deleteUserData(user.getId());

		List<Income> incomes = incomeRepository.findAll();
		List<Expenditure> expenditures = expenditureRepository.findAll();
		List<UserCategory> userCategories = userCategoryRepository.findAll();
		List<Budget> budgets = budgetRepository.findAll();
		List<User> users = userRepository.findAll();

		// then
		assertAll(
			() -> assertThat(incomes).hasSize(0),
			() -> assertThat(expenditures).hasSize(0),
			() -> assertThat(userCategories).hasSize(0),
			() -> assertThat(budgets).hasSize(0),
			() -> assertThat(users).hasSize(0)
		);
	}

	@Test
	void 다른_유저의_데이터는_삭제_안함() {
		// given
		User user2 = save(new User("test2@test.com", "test1234", "testuser2"));
		save(new UserCategory(user2, incomeCategory));
		UserCategory user2Category = save(new UserCategory(user2, expenditureCategory));
		createIncomes(10, user2, user2Category);
		createExpenditures(20, user2, user2Category);
		createBudgets(5, user2, user2Category);

		// when
		userDeleteService.deleteUserData(user.getId());

		List<Income> incomes = incomeRepository.findAll();
		List<Expenditure> expenditures = expenditureRepository.findAll();
		List<UserCategory> userCategories = userCategoryRepository.findAll();
		List<Budget> budgets = budgetRepository.findAll();
		List<User> users = userRepository.findAll();

		// then
		assertAll(
			() -> assertThat(incomes).hasSize(10),
			() -> assertThat(expenditures).hasSize(20),
			() -> assertThat(userCategories).hasSize(2),
			() -> assertThat(budgets).hasSize(5),
			() -> assertThat(users).hasSize(1)
		);
	}

	private void createExpenditures(int count, User user, UserCategory userCategory) {
		for (int i = 0; i < count; i++) {
			expenditureRepository.save(
				new Expenditure(
					LocalDateTime.now(),
					10000L + i,
					"내용" + i,
					expenditureCategory.getName(),
					user,
					userCategory
				)
			);
		}
	}

	private void createIncomes(int count, User user, UserCategory userCategory) {
		for (int i = 0; i < count; i++) {
			incomeRepository.save(
				new Income(
					LocalDateTime.now(),
					10000L + i,
					"내용" + i,
					incomeCategory.getName(),
					user,
					userCategory
				)
			);
		}
	}

	private void createBudgets(int count, User user, UserCategory userCategory) {
		for (int i = 0; i < count; i++) {
			budgetRepository.save(
				new Budget(
					100L,
					YearMonth.now(),
					user,
					userCategory));
		}
	}

}
