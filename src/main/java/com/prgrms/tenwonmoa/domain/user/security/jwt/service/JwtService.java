package com.prgrms.tenwonmoa.domain.user.security.jwt.service;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.user.dto.TokenResponse;
import com.prgrms.tenwonmoa.domain.user.security.jwt.RefreshToken;
import com.prgrms.tenwonmoa.domain.user.security.jwt.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

	private final TokenProvider tokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;

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

}
