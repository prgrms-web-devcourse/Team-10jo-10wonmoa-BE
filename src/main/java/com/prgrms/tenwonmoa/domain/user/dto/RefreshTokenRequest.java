package com.prgrms.tenwonmoa.domain.user.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshTokenRequest {
	@NotBlank(message = "값을 채워주세요.")
	private String accessToken;
	@NotBlank(message = "값을 채워주세요.")
	private String refreshToken;
}
