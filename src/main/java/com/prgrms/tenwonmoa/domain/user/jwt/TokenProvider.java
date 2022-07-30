package com.prgrms.tenwonmoa.domain.user.jwt;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWTCreator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenProvider {

	private final Jwt jwt;

	public String generateToken(Long userId, String email) {
		Date now = new Date();
		JWTCreator.Builder builder = com.auth0.jwt.JWT.create();
		builder.withIssuer(jwt.getIssuer());
		builder.withIssuedAt(now);
		if (jwt.getExpirySeconds() > 0) {
			builder.withExpiresAt(new Date(now.getTime() + jwt.getExpirySeconds()));
		}
		builder.withClaim("userId", userId)
			.withClaim("email", email);
		return builder.sign(jwt.getAlgorithm());
	}

}
