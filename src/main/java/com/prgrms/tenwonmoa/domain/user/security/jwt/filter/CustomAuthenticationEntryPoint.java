package com.prgrms.tenwonmoa.domain.user.security.jwt.filter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.tenwonmoa.exception.message.Message;
import com.prgrms.tenwonmoa.exception.response.ErrorResponse;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException, ServletException {

		String errorMessage = (String)request.getAttribute("exception");
		if (errorMessage == null) {
			errorMessage = Message.NOT_NULL_TOKEN.getMessage();
		}

		ErrorResponse errorResponse =
			new ErrorResponse(List.of(errorMessage), HttpStatus.UNAUTHORIZED.value());

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		try (OutputStream os = response.getOutputStream()) {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.writeValue(os, errorResponse);
			os.flush();
		}
	}
}
