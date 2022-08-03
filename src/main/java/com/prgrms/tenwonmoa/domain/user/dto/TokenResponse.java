package com.prgrms.tenwonmoa.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
	private String accessToken;
	private String accessTokenExpiredIn;
	private String refreshToken;
	private String refreshTokenExpiredIn;
}
