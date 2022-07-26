package com.prgrms.tenwonmoa.exception.message;

import static com.prgrms.tenwonmoa.domain.accountbook.AccountBookConst.*;

import java.text.MessageFormat;

import lombok.Getter;

@Getter
public enum Message {
	INVALID_CONTENT_ERR_MSG(MessageFormat.format("내용은 {0}글자 까지만 가능합니다.", CONTENT_MAX)),
	INVALID_AMOUNT_ERR_MSG(MessageFormat.format("입력 가능 범위는 {0}~{1}입니다.", AMOUNT_MIN, AMOUNT_MAX)),
	ALREADY_EXAMPLE("이미 존재하는 샘플입니다."),
	NOT_FOUND_EXAMPLE("존재하지 않는 샘플입니다."),
	NOT_NULL_REGISTER_DATE("등록 날짜는 필수입니다.");

	private final String message;

	Message(String message) {
		this.message = message;
	}
}
