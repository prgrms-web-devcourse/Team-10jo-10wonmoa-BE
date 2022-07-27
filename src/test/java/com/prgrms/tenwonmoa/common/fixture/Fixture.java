package com.prgrms.tenwonmoa.common.fixture;

import java.time.LocalDate;

import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;

public final class Fixture {
	private Fixture() {
	}

	public static User createUser() {
		return new User("test@gmail.com", "123456789", "testuser");
	}

	public static Category createCategory() {
		return new Category("categoryName", CategoryType.INCOME);
	}

	public static UserCategory createUserCategory() {
		return new UserCategory(createUser(), createCategory());
	}

	public static Income createIncome() {
		UserCategory userCategory = createUserCategory();
		return new Income(LocalDate.now(),
			1000L,
			"content",
			userCategory.getCategory().getName(),
			userCategory.getUser(),
			userCategory);
	}
}
