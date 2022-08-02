package com.prgrms.tenwonmoa.domain.user.jwt;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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
import com.prgrms.tenwonmoa.domain.user.security.jwt.TokenProvider;

@DisplayName("토큰 생성기 테스트")
@ExtendWith(SpringExtension.class)
@Import({TokenProvider.class, Jwt.class})
@EnableConfigurationProperties(JwtConfigure.class)
@TestPropertySource("classpath:jwt-config-test.properties")
class TokenProviderTest {

	@Autowired
	private TokenProvider tokenProvider;

	@Test
	void 토큰_생성_성공() {
		Long userId = 2L;
		String userEmail = "test@test.com";

		String token = tokenProvider.generateToken(userId, userEmail);

		int dotBetweenHeaderAndPayload = token.indexOf('.');
		int dotBetweenPayloadAndSignature = token.lastIndexOf('.');

		String encodedHeader = token.substring(0, dotBetweenHeaderAndPayload);
		String encodedPayload = token.substring(dotBetweenHeaderAndPayload + 1, dotBetweenPayloadAndSignature);

		assertAll(
			() -> assertThat(encodedHeader).isBase64(),
			() -> assertThat(encodedPayload).isBase64()
		);
	}

}
