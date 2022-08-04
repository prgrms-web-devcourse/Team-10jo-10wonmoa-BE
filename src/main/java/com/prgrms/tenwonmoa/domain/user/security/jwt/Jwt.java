package com.prgrms.tenwonmoa.domain.user.security.jwt;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.prgrms.tenwonmoa.config.JwtConfigure;

import lombok.Getter;

@Component
@Getter
public final class Jwt {

	private final JwtConfigure jwtConfigure;

	private final String issuer;

	private final String clientSecret;

	private final int expirySeconds;

	private final int refreshExpirySeconds;

	private final Algorithm algorithm;

	private final JWTVerifier jwtVerifier;

	public Jwt(JwtConfigure jwtConfigure) {
		this.jwtConfigure = jwtConfigure;
		this.issuer = jwtConfigure.getIssuer();
		this.clientSecret = jwtConfigure.getClientSecret();
		this.expirySeconds = jwtConfigure.getExpirySeconds();
		this.refreshExpirySeconds = jwtConfigure.getRefreshExpirySeconds();
		this.algorithm = Algorithm.HMAC512(clientSecret);
		this.jwtVerifier = com.auth0.jwt.JWT.require(algorithm)
			.withIssuer(issuer)
			.build();
	}

	@Getter
	public static class Claims {

		private Long userId;
		private String email;
		private Date iat;
		private Date exp;

		private Claims() {
		}

		public Claims(DecodedJWT decodedJwt) {
			Claim userId = decodedJwt.getClaim("userId");
			if (!userId.isNull()) {
				this.userId = userId.asLong();
			}
			Claim email = decodedJwt.getClaim("email");
			if (!email.isNull()) {
				this.email = email.asString();
			}
			this.iat = decodedJwt.getIssuedAt();
			this.exp = decodedJwt.getExpiresAt();
		}
	}

}
