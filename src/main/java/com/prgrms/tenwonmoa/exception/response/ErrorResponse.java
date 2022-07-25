package com.prgrms.tenwonmoa.exception.response;

import java.util.List;

import lombok.Getter;

@Getter
public class ErrorResponse {
	private final List<String> messages;
	private final int status;

	public ErrorResponse(List<String> messages, int status) {
		this.messages = messages;
		this.status = status;
	}
}
