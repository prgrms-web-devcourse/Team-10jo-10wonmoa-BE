package com.prgrms.tenwonmoa.exception.message;

import static com.prgrms.tenwonmoa.domain.accountbook.AccountBookConst.*;

import java.text.MessageFormat;

import lombok.Getter;

@Getter
public enum Message {

	WRONG_DATE_TIME_FORMAT("잘못된 날짜 양식으로 요청하였습니다"),
	// 공통
	INTERNAL_SERVER_ERROR("예상치 못한 예외가 발생했습니다. 지속적인 예외 발생 시 시스템 관리자에게 문의해주세요"),

	// 사용자
	NO_AUTHENTICATION("다른 사용자의 데이터에 접근할 수 없습니다."),
	USER_NOT_FOUND("해당 사용자는 존재하지 않습니다."),

	// 카테고리
	USER_CATEGORY_NOT_FOUND("해당 사용자 카테고리는 존재하지 않습니다."),
	CATEGORY_NOT_FOUND("해당 카테고리가 존재 하지 않습니다."),

	CATEGORY_NO_AUTHENTICATION("카테고리에 대한 접근권한이 없습니다."),

	// 가계부(수입 지출)
	NOT_NULL_REGISTER_DATE("등록 날짜는 필수입니다."),
	NOT_NULL_AMOUNT("금액은 필수입니다."),
	EXPENDITURE_NOT_FOUND("해당 지출이 존재 하지 않습니다."),
	EXPENDITURE_NO_AUTHENTICATION("지출에 대한 접근권한이 없습니다."),

	INVALID_MIN_MAX_VALUE("최소값은 최대값 보다 작아야 합니다"),

	INVALID_START_END_DATE("시작일은 종료일 전이여야 합니다"),

	// 수입
	INVALID_CONTENT_ERR_MSG(MessageFormat.format("내용은 {0}글자 까지만 가능합니다.", CONTENT_MAX)),
	INVALID_AMOUNT_ERR_MSG(MessageFormat.format("입력 가능 범위는 {0}~{1}입니다.", AMOUNT_MIN, AMOUNT_MAX)),
	INCOME_NOT_FOUND("수입 정보가 존재하지 않습니다."),

	// 유저
	NOT_NULL_EMAIL("이메일은 필수입니다."),
	INVALID_EMAIL_PATTERN("이메일은 형식을 맞춰야 합니다."),
	NOT_NULL_PASSWORD("비밀번호는 필수입니다."),
	INVALID_PASSWORD_LENGTH("비밀번호는 8~20 글자 사이입니다."),
	NOT_NULL_USERNAME("이름은 필수입니다."),
	INVALID_USERNAME_LENGTH("이름은 2~20 글자 사이입니다."),
	INVALID_USERNAME_PATTERN("이름은 특수문자를 포함하지 않습니다."),
	ALREADY_EXISTS_USER("이미 존재하는 유저입니다."),

	// 로그인
	INVALID_EMAIL_OR_PASSWORD("이메일 또는 비밀번호가 잘못되었습니다."),
	EXPIRED_ACCESS_TOKEN("만료된 access-token 입니다."),
	EXPIRED_REFRESH_TOKEN("만료된 refresh-token 입니다."),
	NOT_EXPIRED_ACCESS_TOKEN("유효한 access token 입니다."),
	NOT_NULL_TOKEN("토큰 값이 비었습니다."),
	INVALID_TOKEN("잘못된 토큰 요청입니다."),
	INVALID_REDIRECT_URL("잘못된 redirect url 요청입니다."),
	LOGOUT_USER("이미 로그아웃된 회원입니다."),

	// 달력 요청
	INVALID_YEAR("년도는 1900년에서 3000년 이하입니다."),
	INVALID_MONTH("월은 1월에서 12월 이하입니다."),

	// Page 요쳥
	INVALID_PAGE_NUMBER("해당 페이지는 존재하지 않습니다.");

	private final String message;

	Message(String message) {
		this.message = message;
	}
}
