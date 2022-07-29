package com.prgrms.tenwonmoa.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginUserResponse {
	private String accessToken;
	private String refreshToken;
}
