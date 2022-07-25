package com.prgrms.tenwonmoa.exception.message;

import lombok.Getter;

@Getter
public enum Message {

	ALREADY_EXAMPLE("이미 존재하는 샘플입니다."),
	USER_NOT_FOUND("해당 사용자는 존재하지 않습니다."),
	USER_CATEGORY_NOT_FOUND("해당 사용자 카테고리는 존재하지 않습니다."),
	CATEGORY_NOT_FOUND("해당 카테고리가 존재 하지 않습니다.");

	private final String message;

	Message(String message) {
		this.message = message;
	}
}
