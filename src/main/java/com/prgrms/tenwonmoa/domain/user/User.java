package com.prgrms.tenwonmoa.domain.user;

import static com.google.common.base.Preconditions.*;
import static com.prgrms.tenwonmoa.domain.user.UserConst.*;
import static com.prgrms.tenwonmoa.exception.message.Message.*;
import static lombok.AccessLevel.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.prgrms.tenwonmoa.domain.common.BaseEntity;
import com.prgrms.tenwonmoa.exception.UnauthorizedUserException;
import com.prgrms.tenwonmoa.exception.message.Message;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "user")
@Getter
public class User extends BaseEntity {

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

	public void validateLogin(User findUser) {
		if (!this.equals(findUser)) {
			throw new UnauthorizedUserException(Message.NO_AUTHENTICATION.getMessage());
		}
	}

	private void validateUsername(String username) {
		checkNotNull(username, NOT_NULL_USERNAME.getMessage());
		checkArgument(username.length() >= MIN_USERNAME_LENGTH && username.length() <= MAX_USERNAME_LENGTH,
			INVALID_USERNAME_LENGTH.getMessage());
		checkArgument(username.matches(USERNAME_REGEX), INVALID_USERNAME_PATTERN.getMessage());
	}

	private void validatePassword(String password) {
		checkNotNull(password, NOT_NULL_PASSWORD.getMessage());
	}

	private void validateEmail(String email) {
		checkNotNull(email, NOT_NULL_EMAIL.getMessage());
		checkArgument(email.matches(EMAIL_REGEX), INVALID_EMAIL_PATTERN.getMessage());
	}
}
