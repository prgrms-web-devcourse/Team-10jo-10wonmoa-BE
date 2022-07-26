package com.prgrms.tenwonmoa.exception.message;

import lombok.Getter;

@Getter
public enum Message {

	ALREADY_EXAMPLE("이미 존재하는 샘플입니다."),
	NOT_FOUND_EXAMPLE("존재하지 않는 샘플입니다."),

	// 카테고리
	CATEGORY_NOT_FOUND("존재하지 않는 카테고리입니다");

	private final String message;

	Message(String message) {
		this.message = message;
	}
}
