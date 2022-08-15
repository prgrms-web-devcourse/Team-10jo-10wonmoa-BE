package com.prgrms.tenwonmoa.domain.user;

public final class UserConst {

	private UserConst() {
	}

	public static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
	public static final String USERNAME_REGEX = "([A-Za-z가-힣0-9])*";
	public static final int MIN_USERNAME_LENGTH = 2;
	public static final int MAX_PASSWORD_LENGTH = 20;
	public static final int MAX_USERNAME_LENGTH = 20;
	public static final int MIN_PASSWORD_LENGTH = 8;

}
