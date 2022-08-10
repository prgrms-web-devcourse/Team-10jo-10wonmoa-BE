package com.prgrms.tenwonmoa.domain.user.security.oauth2.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import com.prgrms.tenwonmoa.domain.user.dto.TokenResponse;
import com.prgrms.tenwonmoa.domain.user.security.jwt.service.JwtService;
import com.prgrms.tenwonmoa.domain.user.security.oauth2.OAuth2UserPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private static final String DEFAULT_REDIRECT_URL = "http://localhost:3000/redirect";
	private final JwtService jwtService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws ServletException, IOException {
		OAuth2UserPrincipal principal = (OAuth2UserPrincipal)authentication.getPrincipal();

		log.info("구글 로그인 성공 {}", principal);

		Long userId = principal.getId();
		String email = principal.getEmail();

		TokenResponse tokenResponse = jwtService.generateToken(userId, email);

		String targetUrl = UriComponentsBuilder.fromUriString(DEFAULT_REDIRECT_URL)
			.queryParam("access-token", tokenResponse.getAccessToken())
			.queryParam("refresh-token", tokenResponse.getRefreshToken())
			.build()
			.toUriString();

		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
}
