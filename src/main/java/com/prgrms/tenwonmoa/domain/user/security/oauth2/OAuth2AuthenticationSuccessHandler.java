package com.prgrms.tenwonmoa.domain.user.security.oauth2;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws ServletException, IOException {
		Object principal = authentication.getPrincipal();

		log.info("구글 로그인 성공 {}", principal);

		PrintWriter writer = response.getWriter();
		writer.write(principal.toString());
		writer.flush();
		writer.close();
	}
}
