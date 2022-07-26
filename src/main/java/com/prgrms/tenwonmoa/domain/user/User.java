package com.prgrms.tenwonmoa.domain.user;

import static com.google.common.base.Preconditions.*;
import static com.prgrms.tenwonmoa.exception.message.Message.*;
import static lombok.AccessLevel.*;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.prgrms.tenwonmoa.domain.common.BaseEntity;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "user")
public class User extends BaseEntity {

	private static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
	private static final String USERNAME_REGEX = "([A-Za-z가-힣0-9])*";
	private static final int MAX_USERNAME_LENGTH = 20;
	private static final int MIN_USERNAME_LENGTH = 2;
	private static final int MAX_PASSWORD_LENGTH = 20;
	private static final int MIN_PASSWORD_LENGTH = 8;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "username", nullable = false)
	private String username;

	public User(String email, String password, String username) {
		validateEmail(email);
		validatePassword(password);
		validateUsername(username);
		this.email = email;
		this.password = password;
		this.username = username;
	}

	private void validateUsername(String username) {
		checkArgument(Objects.nonNull(username), NOT_NULL_USERNAME.getMessage());
		checkArgument(username.length() >= MIN_USERNAME_LENGTH && username.length() <= MAX_USERNAME_LENGTH,
			INVALID_USERNAME_LENGTH.getMessage());
		checkArgument(username.matches(USERNAME_REGEX), INVALID_USERNAME_PATTERN.getMessage());
	}

	private void validatePassword(String password) {
		checkArgument(Objects.nonNull(password), NOT_NULL_PASSWORD.getMessage());
		checkArgument(password.length() >= MIN_PASSWORD_LENGTH && password.length() <= MAX_PASSWORD_LENGTH,
			INVALID_PASSWORD_LENGTH.getMessage());
	}

	private void validateEmail(String email) {
		checkArgument(Objects.nonNull(email), NOT_NULL_EMAIL.getMessage());
		checkArgument(email.matches(EMAIL_REGEX), INVALID_EMAIL_PATTERN.getMessage());
	}
}
