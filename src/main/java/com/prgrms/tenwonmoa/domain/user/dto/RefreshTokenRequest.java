package com.prgrms.tenwonmoa.domain.user.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshTokenRequest {
	private static final String TOKEN_NOT_EMPTY_MESSAGE = "토큰값을 채워주세요.";

	@NotBlank(message = TOKEN_NOT_EMPTY_MESSAGE)
	private String accessToken;
	@NotBlank(message = TOKEN_NOT_EMPTY_MESSAGE)
	private String refreshToken;
}
