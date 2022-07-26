package com.prgrms.tenwonmoa.exception.message;

import static com.prgrms.tenwonmoa.domain.accountbook.AccountBookConst.*;

import java.text.MessageFormat;

import lombok.Getter;

@Getter
public enum Message {

	ALREADY_EXAMPLE("이미 존재하는 샘플입니다."),

	NOT_FOUND_EXAMPLE("존재하지 않는 샘플입니다."),

	USER_NOT_FOUND("해당 사용자는 존재하지 않습니다."),


	// 수입
	INVALID_CONTENT_ERR_MSG(MessageFormat.format("내용은 {0}글자 까지만 가능합니다.", CONTENT_MAX)),
	INVALID_AMOUNT_ERR_MSG(MessageFormat.format("입력 가능 범위는 {0}~{1}입니다.", AMOUNT_MIN, AMOUNT_MAX)),
	NOT_NULL_REGISTER_DATE("등록 날짜는 필수입니다."),
	NOT_NULL_AMOUNT("금액은 필수입니다."),

	// 카테고리
	USER_CATEGORY_NOT_FOUND("해당 사용자 카테고리는 존재하지 않습니다."),
	CATEGORY_NOT_FOUND("해당 카테고리가 존재 하지 않습니다.");

	private final String message;

	Message(String message) {
		this.message = message;
	}
}
