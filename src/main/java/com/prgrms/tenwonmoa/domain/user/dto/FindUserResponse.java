package com.prgrms.tenwonmoa.domain.user.dto;

import com.prgrms.tenwonmoa.domain.user.User;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FindUserResponse {

	private final String email;

	private final String username;

	public static FindUserResponse of(User user) {
		return new FindUserResponse(user.getEmail(), user.getUsername());
	}
}
