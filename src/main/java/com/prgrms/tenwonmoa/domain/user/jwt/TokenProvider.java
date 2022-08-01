package com.prgrms.tenwonmoa.domain.user.jwt;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.interfaces.DecodedJWT;

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

	public Long validateAndGetUserId(String token) {
		Jwt.Claims claims = verifyAndGetClaims(token);
		return claims.getUserId();
	}

	private Jwt.Claims verifyAndGetClaims(String token) {
		DecodedJWT decodedJwt = jwt.getJwtVerifier().verify(token);
		return new Jwt.Claims(decodedJwt);
	}

}
