package com.prgrms.tenwonmoa.domain.user.dto;

import static com.prgrms.tenwonmoa.domain.user.UserConst.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.prgrms.tenwonmoa.domain.user.User;

import lombok.Getter;

@Getter
public class CreateUserRequest {

	@Email(regexp = EMAIL_REGEX, message = "이메일 형식을 지켜주세요")
	@NotBlank(message = "이메일을 채워주세요")
	private String email;

	@Size(min = MIN_USERNAME_LENGTH, max = MAX_USERNAME_LENGTH, message = "이름의 길이를 맞춰주세요")
	@Pattern(regexp = USERNAME_REGEX, message = "이름 형식을 지켜주세요")
	@NotBlank(message = "이름을 채워주세요")
	private String username;

	@Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH, message = "비밀번호 길이를 맞춰주세요")
	@NotBlank(message = "비밀번호를 채워주세요")
	private String password;

	public CreateUserRequest(String email, String username, String password) {
		this.email = email;
		this.username = username;
		this.password = password;
	}

	public User toEntity() {
		return new User(email, password, username);
	}

}
