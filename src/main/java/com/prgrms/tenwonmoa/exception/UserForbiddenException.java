package com.prgrms.tenwonmoa.exception;

public class UserForbiddenException extends RuntimeException {

	private final String message;

	public UserForbiddenException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
