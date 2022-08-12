package com.prgrms.tenwonmoa.domain.user.security.jwt;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Getter;

@Getter
@RedisHash(value = "logoutAccessToken")
public class LogoutAccessToken {
	@Id
	private String token;

	private String email;

	@TimeToLive
	private Long expirationSecs;

	public LogoutAccessToken(String token, String email, Long expirationSecs) {
		this.token = token;
		this.email = email;
		this.expirationSecs = expirationSecs / 1000;
	}
}
