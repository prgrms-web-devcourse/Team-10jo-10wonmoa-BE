package com.prgrms.tenwonmoa.domain.user.jwt;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.prgrms.tenwonmoa.config.JwtConfigure;

@DisplayName("JWT 도메인 테스트")
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(JwtConfigure.class)
@TestPropertySource("classpath:jwt-config-test.properties")
class JwtTest {

	@Autowired
	private JwtConfigure jwtConfigure;

	@Test
	void jwt_설정_바인딩_테스트() {
		assertAll(
			() -> assertThat(jwtConfigure.getHeader()).isEqualTo("testToken"),
			() -> assertThat(jwtConfigure.getIssuer()).isEqualTo("testIssuer"),
			() -> assertThat(jwtConfigure.getExpirySeconds()).isEqualTo(6000),
			() -> assertThat(jwtConfigure.getRefreshExpirySeconds()).isEqualTo(600000)
		);
	}

}
