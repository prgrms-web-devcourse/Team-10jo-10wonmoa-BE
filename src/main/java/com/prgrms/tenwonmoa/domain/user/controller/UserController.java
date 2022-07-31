package com.prgrms.tenwonmoa.domain.user.controller;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

	private final UserService userService;

	@PostMapping
	public ResponseEntity<Void> signup(@Valid @RequestBody CreateUserRequest createUserRequest) {
		userService.createUser(createUserRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/login")
	public ResponseEntity<Void> login(@Valid @RequestBody LoginUserRequest loginUserRequest) {
		LoginUserResponse loginResponse =
			userService.login(loginUserRequest.getEmail(), loginUserRequest.getPassword());

		String accessToken = loginResponse.getAccessToken();

		return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, accessToken).build();
	}

}
