package com.prgrms.tenwonmoa.domain.user.controller;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.tenwonmoa.domain.user.dto.CreateUserRequest;
import com.prgrms.tenwonmoa.domain.user.dto.LoginUserRequest;
import com.prgrms.tenwonmoa.domain.user.dto.LoginUserResponse;
import com.prgrms.tenwonmoa.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

	private static final String ACCESS_TOKEN = "access-token";

	private final UserService userService;

	@PostMapping
	public ResponseEntity<Void> signup(@Valid @RequestBody CreateUserRequest createUserRequest) {
		userService.createUser(createUserRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/login")
	public ResponseEntity<LoginUserResponse> login(@Valid @RequestBody LoginUserRequest loginUserRequest) {
		LoginUserResponse loginResponse =
			userService.login(loginUserRequest.getEmail(), loginUserRequest.getPassword());

		String accessToken = loginResponse.getAccessToken();
		ResponseCookie accessTokenCookie = generateAccessTokenCookie(accessToken);

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
			.body(loginResponse);
	}

	private ResponseCookie generateAccessTokenCookie(String accessToken) {
		return ResponseCookie.from(ACCESS_TOKEN, accessToken)
			.httpOnly(true)
			.build();
	}

}
