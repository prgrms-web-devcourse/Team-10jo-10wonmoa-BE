package com.prgrms.tenwonmoa.domain.user.dto;

import static com.prgrms.tenwonmoa.domain.user.UserConst.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.prgrms.tenwonmoa.domain.user.User;

public class CreateUserRequest {

	@Email(regexp = EMAIL_REGEX)
	@NotBlank
	private String email;

	@Size(min = MIN_USERNAME_LENGTH, max = MAX_USERNAME_LENGTH)
	@Pattern(regexp = USERNAME_REGEX)
	@NotBlank
	private String username;

	@Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH)
	@NotBlank
	private String password;

	public CreateUserRequest(String email, String username, String password) {
		this.email = email;
		this.username = username;
		this.password = password;
	}

	public User toEntity() {
		return new User(email, password, username);
	}

	public String getEmail() {
		return email;
	}
}
