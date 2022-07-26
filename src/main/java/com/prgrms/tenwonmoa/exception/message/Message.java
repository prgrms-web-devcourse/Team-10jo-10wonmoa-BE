package com.prgrms.tenwonmoa.exception.message;

import static com.prgrms.tenwonmoa.domain.accountbook.AccountBookConst.*;

import java.text.MessageFormat;

import lombok.Getter;

@Getter
public enum Message {
	INVALID_CONTENT_ERR_MSG(MessageFormat.format("내용은 {0}글자 까지만 가능합니다.", CONTENT_MAX)),
	INVALID_AMOUNT_ERR_MSG(MessageFormat.format("입력 가능 범위는 {0}~{1}입니다.", AMOUNT_MIN, AMOUNT_MAX)),
	ALREADY_EXAMPLE("이미 존재하는 샘플입니다."),
	USER_NOT_FOUND("해당 사용자는 존재하지 않습니다."),
	USER_CATEGORY_NOT_FOUND("해당 사용자 카테고리는 존재하지 않습니다."),
	CATEGORY_NOT_FOUND("해당 카테고리가 존재 하지 않습니다."),
	NOT_FOUND_EXAMPLE("존재하지 않는 샘플입니다."),
	NOT_NULL_REGISTER_DATE("등록 날짜는 필수입니다."),
	NOT_NULL_AMOUNT("금액은 필수입니다."),


	// 유저
	NOT_NULL_EMAIL("이메일은 필수입니다."),
	INVALID_EMAIL_PATTERN("이메일은 형식을 맞춰야 합니다."),
	NOT_NULL_PASSWORD("비밀번호는 필수입니다."),
	INVALID_PASSWORD_LENGTH("비밀번호는 8~20 글자 사이입니다."),
	NOT_NULL_USERNAME("이름은 필수입니다."),
	INVALID_USERNAME_LENGTH("이름은 2~20 글자 사이입니다."),
	INVALID_USERNAME_PATTERN("이름은 특수문자를 포함하지 않습니다.");


	private final String message;

	Message(String message) {
		this.message = message;
	}
}
