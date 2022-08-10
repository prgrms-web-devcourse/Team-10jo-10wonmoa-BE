package com.prgrms.tenwonmoa.domain.user.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.tenwonmoa.domain.user.dto.CreateUserRequest;
import com.prgrms.tenwonmoa.domain.user.dto.FindUserResponse;
import com.prgrms.tenwonmoa.domain.user.dto.LoginUserRequest;
import com.prgrms.tenwonmoa.domain.user.dto.RefreshTokenRequest;
import com.prgrms.tenwonmoa.domain.user.dto.TokenResponse;
import com.prgrms.tenwonmoa.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

	private final UserService userService;

	@GetMapping
	public ResponseEntity<FindUserResponse> getUserInfo(@AuthenticationPrincipal Long userId){
		return ResponseEntity.ok(FindUserResponse.of(userService.findById(userId)));
	}

	@PostMapping
	public ResponseEntity<Void> signup(@Valid @RequestBody CreateUserRequest createUserRequest) {
		userService.createUser(createUserRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/login")
	public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginUserRequest loginUserRequest) {
		TokenResponse loginResponse = userService.login(loginUserRequest.getEmail(), loginUserRequest.getPassword());
		return ResponseEntity.ok().body(loginResponse);
	}

	@PostMapping("/refresh")
	public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
		TokenResponse refreshResponse = userService.refresh(refreshTokenRequest.getAccessToken(),
			refreshTokenRequest.getRefreshToken());
		return ResponseEntity.ok().body(refreshResponse);
	}

}
