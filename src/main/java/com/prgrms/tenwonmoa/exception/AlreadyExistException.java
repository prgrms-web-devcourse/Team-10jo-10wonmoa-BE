package com.prgrms.tenwonmoa.exception;

public class AlreadyExistException extends RuntimeException {

	private final Message message;

	public AlreadyExistException(Message message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message.getMessage();
	}
}
