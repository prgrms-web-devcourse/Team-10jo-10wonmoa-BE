package com.prgrms.tenwonmoa.domain.user.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.prgrms.tenwonmoa.domain.user.security.jwt.service.TokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private static final String BEARER_PREFIX = "Bearer ";
	private final TokenProvider tokenProvider;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// SecurityContext에 이미 token이 들어있음
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			log.debug("SecurityContextHolder에 이미 다른 security token이 들어있음: '{}'",
				SecurityContextHolder.getContext().getAuthentication());
			filterChain.doFilter(request, response);
			return;
		}

		// 요청에서 토큰 가져오기
		String token = parseToken(request);

		if (StringUtils.hasText(token)) {
			try {
				// userId 가져오기
				Long userId = tokenProvider.validateAndGetUserId(token);
				log.info("Authenticated user Id: {}", userId);

				// 인증 완료, SecurityContextHolder 에 인증된 사용자라고 등록
				AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					userId,
					null,
					AuthorityUtils.NO_AUTHORITIES
				);
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (Exception e) {
				log.warn("Jwt processing 실패: {}", e.getMessage());
			}
		}

		filterChain.doFilter(request, response);
	}

	private String parseToken(HttpServletRequest request) {
		// Http 요청의 헤더를 파싱해 Bearer 토큰 리턴
		String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
		log.info("Bearer Token: {}", bearerToken);

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(BEARER_PREFIX.length());
		}
		return null;
	}
}