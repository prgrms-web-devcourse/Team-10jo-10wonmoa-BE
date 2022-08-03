package com.prgrms.tenwonmoa.domain.user.security.jwt.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.user.security.jwt.RefreshToken;
import com.prgrms.tenwonmoa.domain.user.security.jwt.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;

	@Transactional
	public void updateToken(RefreshToken token) {
		refreshTokenRepository.save(token);
	}

}
