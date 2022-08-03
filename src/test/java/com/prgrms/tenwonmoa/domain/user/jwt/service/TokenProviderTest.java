package com.prgrms.tenwonmoa.domain.user.jwt.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.prgrms.tenwonmoa.config.JwtConfigure;
import com.prgrms.tenwonmoa.domain.user.security.jwt.Jwt;
import com.prgrms.tenwonmoa.domain.user.security.jwt.service.TokenProvider;

@DisplayName("토큰 생성기 테스트")
@ExtendWith(SpringExtension.class)
@Import({TokenProvider.class, Jwt.class})
@EnableConfigurationProperties(JwtConfigure.class)
@TestPropertySource("classpath:jwt-config-test.properties")
class TokenProviderTest {

	@Autowired
	private TokenProvider tokenProvider;

	@Test
	void base64_access_토큰_생성() {
		Long userId = 2L;
		String userEmail = "test@test.com";

		String token = tokenProvider.generateAccessToken(userId, userEmail, new Date());

		int dotBetweenHeaderAndPayload = token.indexOf('.');
		int dotBetweenPayloadAndSignature = token.lastIndexOf('.');

		String encodedHeader = token.substring(0, dotBetweenHeaderAndPayload);
		String encodedPayload = token.substring(dotBetweenHeaderAndPayload + 1, dotBetweenPayloadAndSignature);

		assertAll(
			() -> assertThat(encodedHeader).isBase64(),
			() -> assertThat(encodedPayload).isBase64()
		);
	}

	@Test
	void access_token_생성_검증() {
		// given
		Long userId = 3L;
		String userEmail = "test@test.com";

		// when
		String token = tokenProvider.generateAccessToken(userId, userEmail, new Date());
		Long userIdInToken = tokenProvider.validateAndGetUserId(token);

		// then
		assertThat(userId).isEqualTo(userIdInToken);
	}

	@Test
	void refresh_token_생성_검증() {
		// given
		String userEmail = "test@test.com";

		// when
		String token = tokenProvider.generateRefreshToken(userEmail, new Date());
		String emailInToken = tokenProvider.validateAndGetEmail(token);

		// then
		assertThat(userEmail).isEqualTo(emailInToken);
	}

}
