package com.prgrms.tenwonmoa.common.fixture;

import com.prgrms.tenwonmoa.domain.user.User;

public final class Fixture {
	private Fixture() {
	}

	public static User createUser() {
		return new User("test@gmail.com", "123456789", "testuser");
	}

}
