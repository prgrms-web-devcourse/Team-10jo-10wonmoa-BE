package com.prgrms.tenwonmoa.exception;

public class NotFoundException extends RuntimeException {

	private final Message message;

	public NotFoundException(Message message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return this.message.getMessage();
	}
}