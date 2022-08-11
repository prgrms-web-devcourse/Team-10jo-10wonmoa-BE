package com.prgrms.tenwonmoa.domain.user.security.jwt.service;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.dto.TokenResponse;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;
import com.prgrms.tenwonmoa.domain.user.security.jwt.LogoutAccessToken;
import com.prgrms.tenwonmoa.domain.user.security.jwt.RefreshToken;
import com.prgrms.tenwonmoa.domain.user.security.jwt.repository.LogoutAccessTokenRedisRepository;
import com.prgrms.tenwonmoa.domain.user.security.jwt.repository.RefreshTokenRepository;
import com.prgrms.tenwonmoa.exception.message.Message;

@DisplayName("JwtService 테스트")
@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

	@Mock
	private TokenProvider tokenProvider;

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;

	@InjectMocks
	private JwtService jwtService;

	@Test
	void access_refresh_토큰_생성() {
		// given
		Long userId = 1L;
		String email = "test@test.com";

		String accessTokenValue = "dsvjkrtglffdasfds";
		String refreshTokenValue = "fvjckarrvczvcxvd";
		RefreshToken refreshToken = new RefreshToken(email, refreshTokenValue);

		given(tokenProvider.generateAccessToken(any(), any(), any())).willReturn(accessTokenValue);
		given(tokenProvider.generateRefreshToken(any(), any())).willReturn(refreshTokenValue);
		given(refreshTokenRepository.save(any())).willReturn(refreshToken);

		// when
		TokenResponse tokenResponse = jwtService.generateToken(userId, email);

		// then
		assertAll(
			() -> verify(refreshTokenRepository).save(any(RefreshToken.class)),
			() -> assertThat(tokenResponse.getAccessToken()).isEqualTo(accessTokenValue),
			() -> assertThat(tokenResponse.getRefreshToken()).isEqualTo(refreshTokenValue)
		);
	}

	@Test
	void access_토큰_재발급_성공() {
		String accessTokenValue = "dsvjkrtglffdasfds";
		String newAccessTokenValue = "newjkrtglffdasfds";
		String refreshTokenValue = "fvjckarrvczvcxvd";
		User user = createUser();
		String email = user.getEmail();
		RefreshToken refreshToken = new RefreshToken(email, refreshTokenValue);

		given(tokenProvider.isTokenExpired(accessTokenValue)).willReturn(true);
		given(tokenProvider.isTokenExpired(refreshTokenValue)).willReturn(false);
		given(tokenProvider.validateAndGetEmail(any())).willReturn(email);
		given(refreshTokenRepository.findById(any())).willReturn(Optional.of(refreshToken));
		given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
		given(tokenProvider.generateAccessToken(any(), any(), any())).willReturn(newAccessTokenValue);

		TokenResponse tokenResponse = jwtService.refreshToken(accessTokenValue, refreshTokenValue);

		assertAll(
			() -> assertThat(tokenResponse.getAccessToken()).isEqualTo(newAccessTokenValue),
			() -> assertThat(tokenResponse.getRefreshToken()).isEqualTo(refreshTokenValue)
		);
	}

	@Test
	void access_토큰이_유효하면_재발급_실패() {
		String accessTokenValue = "dsvjkrtglffdasfds";
		String refreshTokenValue = "fvjckarrvczvcxvd";

		given(tokenProvider.isTokenExpired(accessTokenValue)).willReturn(false);

		assertThatIllegalArgumentException()
			.isThrownBy(() -> jwtService.refreshToken(accessTokenValue, refreshTokenValue))
			.withMessageContaining(Message.NOT_EXPIRED_ACCESS_TOKEN.getMessage());
	}

	@Test
	void refresh_토큰의_유효기간이_지나면_access_토큰_재발급_실패() {
		String accessTokenValue = "dsvjkrtglffdasfds";
		String refreshTokenValue = "fvjckarrvczvcxvd";

		given(tokenProvider.isTokenExpired(accessTokenValue)).willReturn(true);
		given(tokenProvider.isTokenExpired(refreshTokenValue)).willReturn(true);

		assertThatThrownBy(() -> jwtService.refreshToken(accessTokenValue, refreshTokenValue))
			.isInstanceOf(JWTVerificationException.class)
			.hasMessage(Message.EXPIRED_REFRESH_TOKEN.getMessage());
	}

	@Test
	void 로그아웃_상태에서_access_토큰_재발급_실패() {
		String accessTokenValue = "dsvjkrtglffdasfds";
		String refreshTokenValue = "fvjckarrvczvcxvd";
		User user = createUser();
		String email = user.getEmail();

		given(tokenProvider.isTokenExpired(accessTokenValue)).willReturn(true);
		given(tokenProvider.isTokenExpired(refreshTokenValue)).willReturn(false);
		given(tokenProvider.validateAndGetEmail(any())).willReturn(email);
		given(refreshTokenRepository.findById(any())).willReturn(Optional.empty());

		assertThatThrownBy(() -> jwtService.refreshToken(accessTokenValue, refreshTokenValue))
			.isInstanceOf(JWTVerificationException.class)
			.hasMessage(Message.INVALID_TOKEN.getMessage());
	}

	@Test
	void logout_access_token_생성() {
		String accessTokenValue = "dsvjkrtglffdasfds";
		Long remainMilliSecs = 1032441L;
		String email = "test@test.com";
		LogoutAccessToken logoutAccessToken = new LogoutAccessToken(accessTokenValue, email, remainMilliSecs);

		given(tokenProvider.getRemainMilliSeconds(any(String.class))).willReturn(remainMilliSecs);
		given(logoutAccessTokenRedisRepository.save(any())).willReturn(logoutAccessToken);

		jwtService.generateLogoutAccessToken(email, accessTokenValue);

		verify(logoutAccessTokenRedisRepository).save(any(LogoutAccessToken.class));
	}

}
