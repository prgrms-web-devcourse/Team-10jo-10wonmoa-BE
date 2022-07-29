package com.prgrms.tenwonmoa.common.fixture;

import java.time.LocalDateTime;
import java.util.Random;

import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;

public final class Fixture {
	private Fixture() {
	}

	private static String makeUserName() {
		int leftLimit = 'a';
		int rightLimit = 'z';
		int targetStringLength = 10;
		Random random = new Random();
		StringBuilder buffer = new StringBuilder(targetStringLength);
		for (int i = 0; i < targetStringLength; i++) {
			int randomLimitedInt = leftLimit + (int)
				(random.nextFloat() * (rightLimit - leftLimit + 1));
			buffer.append((char)randomLimitedInt);
		}
		return buffer.toString();
	}

	public static User createUser() {
		String userName = makeUserName();
		return new User(userName + "@gmail.com", "123456789", userName);
	}

	public static Category createCategory() {
		return new Category("categoryName", CategoryType.INCOME);
	}

	public static UserCategory createUserCategory() {
		return new UserCategory(createUser(), createCategory());
	}

	public static Income createIncome() {
		UserCategory userCategory = createUserCategory();
		return new Income(LocalDateTime.now(),
			1000L,
			"content",
			userCategory.getCategory().getName(),
			userCategory.getUser(),
			userCategory);
	}
}
