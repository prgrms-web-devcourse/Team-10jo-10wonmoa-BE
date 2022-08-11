package com.prgrms.tenwonmoa.domain.user.dto;

import com.prgrms.tenwonmoa.domain.user.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FindUserResponse {

	private final String email;

	public static FindUserResponse of(User user) {
		return new FindUserResponse(user.getEmail());
	}
}
