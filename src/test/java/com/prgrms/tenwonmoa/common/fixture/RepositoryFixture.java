package com.prgrms.tenwonmoa.common.fixture;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;

import java.time.LocalDateTime;

import com.prgrms.tenwonmoa.common.RepositoryTest;
import com.prgrms.tenwonmoa.domain.accountbook.Income;
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

	public Income saveIncome() {
		UserCategory userCategory = saveUserCategory();
		return save(new Income(LocalDateTime.now(),
			1000L,
			"content",
			userCategory.getCategory().getName(),
			userCategory.getUser(),
			userCategory));
	}
}
