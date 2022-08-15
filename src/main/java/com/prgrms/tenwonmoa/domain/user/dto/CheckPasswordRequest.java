package com.prgrms.tenwonmoa.domain.user.dto;

import static com.prgrms.tenwonmoa.domain.user.UserConst.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CheckPasswordRequest {

	@Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH, message = "비밀번호 길이를 맞춰주세요")
	@NotBlank(message = "비밀번호를 채워주세요")
	private String password;

}
