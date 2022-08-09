package com.prgrms.tenwonmoa.common.fixture;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import com.prgrms.tenwonmoa.common.RepositoryTest;
import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.budget.Budget;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;

public class RepositoryFixture extends RepositoryTest {

	public User saveRandomUser() {
		return save(createRandomUser());
	}

	public Category saveIncomeCategory() {
		return save(createIncomeCategory());
	}

	public UserCategory saveUserCategory() {
		return save(new UserCategory(saveRandomUser(), saveIncomeCategory()));
	}

	public UserCategory saveUserCategory(User user, Category category) {
		return save(new UserCategory(user, category));
	}

	public Income saveIncome() {
		UserCategory userCategory = saveUserCategory();
		return save(new Income(LocalDateTime.now(), 1000L, "content", userCategory.getCategory().getName(),
			userCategory.getUser(), userCategory));
	}

	public Income saveIncome(UserCategory userCategory, Long amount, LocalDateTime registerDate) {
		return save(new Income(registerDate, amount, "content", userCategory.getCategoryName(), userCategory.getUser(),
			userCategory));
	}

	public Expenditure saveExpenditure(UserCategory userCategory, Long amount, LocalDateTime registerDate) {
		return save(
			new Expenditure(registerDate, amount, "content", userCategory.getCategoryName(), userCategory.getUser(),
				userCategory));
	}

	public Budget saveBudget(Long amount, YearMonth registerDate, User user, UserCategory userCategory) {
		return save(new Budget(100L, registerDate, user, userCategory));
	}

	public void iterateFixture(int count, IntConsumer function) {
		count++;
		IntStream.range(1, count).forEach(i -> {
			function.accept(i);
		});
	}
}
