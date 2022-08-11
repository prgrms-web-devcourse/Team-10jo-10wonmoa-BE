package com.prgrms.tenwonmoa.domain.user.security.jwt.service;

import java.util.Date;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.dto.TokenResponse;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;
import com.prgrms.tenwonmoa.domain.user.security.jwt.LogoutAccessToken;
import com.prgrms.tenwonmoa.domain.user.security.jwt.RefreshToken;
import com.prgrms.tenwonmoa.domain.user.security.jwt.repository.LogoutAccessTokenRedisRepository;
import com.prgrms.tenwonmoa.domain.user.security.jwt.repository.RefreshTokenRepository;
import com.prgrms.tenwonmoa.exception.message.Message;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

	private final TokenProvider tokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;
	private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;

	@Transactional
	public TokenResponse generateToken(Long userId, String email) {
		Date now = new Date();
		String accessToken = tokenProvider.generateAccessToken(userId, email, now);
		String refreshToken = tokenProvider.generateRefreshToken(email, now);
		updateRefreshToken(new RefreshToken(email, refreshToken));

		return new TokenResponse(accessToken, refreshToken);
	}

	private void updateRefreshToken(RefreshToken token) {
		refreshTokenRepository.save(token);
	}

	@Transactional
	public TokenResponse refreshToken(String accessToken, String refreshToken) {
		if (!tokenProvider.isTokenExpired(accessToken)) {    // 만료가 안 된  access-token 이면 에러
			throw new IllegalArgumentException(Message.NOT_EXPIRED_ACCESS_TOKEN.getMessage());
		}
		validateRefreshToken(refreshToken);

		String email = tokenProvider.validateAndGetEmail(refreshToken);

		// 못 찾으면 탈퇴한 회원
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new NoSuchElementException(Message.USER_NOT_FOUND.getMessage()));

		String newAccessToken = tokenProvider.generateAccessToken(user.getId(), user.getEmail(), new Date());

		return new TokenResponse(newAccessToken, refreshToken);
	}

	private void validateRefreshToken(String refreshToken) {
		if (tokenProvider.isTokenExpired(refreshToken)) {    // refresh-token 만료 되었으면
			throw new JWTVerificationException(Message.EXPIRED_REFRESH_TOKEN.getMessage());
		}

		String email = tokenProvider.validateAndGetEmail(refreshToken);

		// refresh-token 의 userEmail 이 DB에 없다. -> 탈퇴한 회원이거나 로그아웃한 회원
		RefreshToken savedRefreshToken = refreshTokenRepository.findById(email)
			.orElseThrow(() -> new JWTVerificationException(Message.INVALID_TOKEN.getMessage()));

		// DB에 저장되어 있는 refresh-token 과 비교
		if (!savedRefreshToken.getToken().equals(refreshToken)) {
			throw new JWTVerificationException(Message.INVALID_TOKEN.getMessage());
		}
	}

	@Transactional
	public void generateLogoutAccessToken(String email, String accessToken) {
		Long remainMilliSeconds = tokenProvider.getRemainMilliSeconds(accessToken);
		logoutAccessTokenRedisRepository.save(new LogoutAccessToken(accessToken, email, remainMilliSeconds));
	}

	public void deleteRefreshToken(String email) {
		refreshTokenRepository.deleteById(email);
	}
}
