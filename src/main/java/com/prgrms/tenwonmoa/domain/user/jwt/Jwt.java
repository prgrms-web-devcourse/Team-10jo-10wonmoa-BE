package com.prgrms.tenwonmoa.domain.user.jwt;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.prgrms.tenwonmoa.config.JwtConfigure;

import lombok.Getter;

@Component
@Getter
public final class Jwt {

	private final JwtConfigure jwtConfigure;

	private final String issuer;

	private final String clientSecret;

	private final int expirySeconds;

	private final Algorithm algorithm;

	private final JWTVerifier jwtVerifier;

	public Jwt(JwtConfigure jwtConfigure) {
		this.jwtConfigure = jwtConfigure;
		this.issuer = jwtConfigure.getIssuer();
		this.clientSecret = jwtConfigure.getClientSecret();
		this.expirySeconds = jwtConfigure.getExpirySeconds();
		this.algorithm = Algorithm.HMAC512(clientSecret);
		this.jwtVerifier = com.auth0.jwt.JWT.require(algorithm)
			.withIssuer(issuer)
			.build();
	}

}
