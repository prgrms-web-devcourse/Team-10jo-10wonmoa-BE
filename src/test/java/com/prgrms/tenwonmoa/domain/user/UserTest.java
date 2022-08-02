package com.prgrms.tenwonmoa.domain.user;

import static com.prgrms.tenwonmoa.exception.message.Message.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("유저 도메인 테스트")
class UserTest {

	@Test
	void 유저_생성_성공() {
		User user = new User("a@a.com", "12345678", "테스트user");
		assertThat(user).isNotNull();
	}

	@Test
	void 이메일이_null_일_때_예외() {
		assertThatThrownBy(() -> new User(null, "12345678", "yanju"))
			.isInstanceOf(NullPointerException.class)
			.hasMessageContaining(NOT_NULL_EMAIL.getMessage());
	}

	@Test
	void 이메일_패턴이_안_맞을_때_예외() {
		String invalidEmail1 = "a";
		String invalidEmail2 = "a@";
		String invalidEmail3 = "@a";

		assertAll(
			() -> assertThatThrownBy(() -> new User(invalidEmail1, "12345678", "yanju"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining(INVALID_EMAIL_PATTERN.getMessage()),
			() -> assertThatThrownBy(() -> new User(invalidEmail2, "12345678", "yanju"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining(INVALID_EMAIL_PATTERN.getMessage()),
			() -> assertThatThrownBy(() -> new User(invalidEmail3, "12345678", "yanju"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining(INVALID_EMAIL_PATTERN.getMessage())
		);
	}

	@Test
	void 비밀번호가_null_일_때_예외() {
		assertThatThrownBy(() -> new User("a@a.com", null, "yanju"))
			.isInstanceOf(NullPointerException.class)
			.hasMessageContaining(NOT_NULL_PASSWORD.getMessage());
	}

	@Test
	void 이름이_null_일_때_예외() {
		assertThatThrownBy(() -> new User("a@a.com", "12345678", null))
			.isInstanceOf(NullPointerException.class)
			.hasMessageContaining(NOT_NULL_USERNAME.getMessage());
	}

	@Test
	void 이름의_길이는_2에서_20_사이가_아니면_예외() {
		String invalidName1 = "s";
		String invalidName2 = "한";
		String invalidName3 = "abcdefghijklmnopqrstu";

		assertAll(
			() -> assertThatThrownBy(() -> new User("a@a.com", "12345678", invalidName1))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining(INVALID_USERNAME_LENGTH.getMessage()),
			() -> assertThatThrownBy(() -> new User("a@a.com", "12345678", invalidName2))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining(INVALID_USERNAME_LENGTH.getMessage()),
			() -> assertThatThrownBy(() -> new User("a@a.com", "12345678", invalidName3))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining(INVALID_USERNAME_LENGTH.getMessage())
		);
	}

	@Test
	void 이름에_특수문자가_들어가면_예외() {
		String invalidName1 = "sam!";
		String invalidName2 = "한*";
		String invalidName3 = "()_gks";

		assertAll(
			() -> assertThatThrownBy(() -> new User("a@a.com", "12345678", invalidName1))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining(INVALID_USERNAME_PATTERN.getMessage()),
			() -> assertThatThrownBy(() -> new User("a@a.com", "12345678", invalidName2))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining(INVALID_USERNAME_PATTERN.getMessage()),
			() -> assertThatThrownBy(() -> new User("a@a.com", "12345678", invalidName3))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining(INVALID_USERNAME_PATTERN.getMessage())
		);
	}
}
