package com.prgrms.tenwonmoa.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginUserResponse {
	private String accessToken;
	private String refreshToken;
}
