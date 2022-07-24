package com.prgrms.tenwonmoa.exception;

import com.prgrms.tenwonmoa.exception.message.Message;

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
