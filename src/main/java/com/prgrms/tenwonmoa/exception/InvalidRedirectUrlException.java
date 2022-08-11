package com.prgrms.tenwonmoa.exception;

import com.prgrms.tenwonmoa.exception.message.Message;

public class InvalidRedirectUrlException extends RuntimeException {

	private final Message message;

	public InvalidRedirectUrlException(Message message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message.getMessage();
	}

}
