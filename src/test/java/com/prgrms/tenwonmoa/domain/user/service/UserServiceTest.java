package com.prgrms.tenwonmoa.domain.user.service;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.prgrms.tenwonmoa.domain.category.service.CreateDefaultUserCategoryService;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.dto.CreateUserRequest;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;
import com.prgrms.tenwonmoa.domain.user.security.jwt.service.JwtService;
import com.prgrms.tenwonmoa.exception.AlreadyExistException;
import com.prgrms.tenwonmoa.exception.message.Message;

@DisplayName("유저 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private UserRepository userRepository;

	@Mock
	private CreateDefaultUserCategoryService createDefaultUserCategoryService;

	@Mock
	private JwtService jwtService;

	@InjectMocks
	private UserService userService;

	private final User user = createUser();

	@Test
	void 아이디로_유저조회_성공() {
		given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

		User findUser = userService.findById(user.getId());
		assertAll(
			() -> assertThat(findUser.getId()).isEqualTo(user.getId()),
			() -> verify(userRepository).findById(user.getId())
		);
	}

	@Test
	void 아이디로_유저조회_실패() {
		given(userRepository.findById(any())).willThrow(
			new NoSuchElementException(Message.USER_NOT_FOUND.getMessage()));

		assertThatThrownBy(() -> userService.findById(any()))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage(Message.USER_NOT_FOUND.getMessage());
	}

	@Test
	void 유저_생성_성공() {
		CreateUserRequest createUserRequest = new CreateUserRequest("test@test.com", "testuser", "12345678");
		String encryptPassword = new BCryptPasswordEncoder().encode(createUserRequest.getPassword());
		User user = new User(createUserRequest.getEmail(), encryptPassword, createUserRequest.getUsername());

		given(passwordEncoder.encode(any(String.class))).willReturn(encryptPassword);
		given(userRepository.existsByEmail(any(String.class))).willReturn(false);
		given(userRepository.save(any(User.class))).willReturn(user);

		userService.createUser(createUserRequest);

		verify(userRepository).save(user);
		verify(createDefaultUserCategoryService).createDefaultUserCategory(user);
	}

	@Test
	void 중복된_이메일이면_유저_생성_실패() {
		String duplicateEmail = "test@gmail.com";
		User user = new User(duplicateEmail, "12345678", "testuser1");

		given(userRepository.existsByEmail(any(String.class))).willReturn(true);

		CreateUserRequest createUserRequest = new CreateUserRequest(duplicateEmail, "123456789", "testuser2");

		assertThatThrownBy(() -> userService.createUser(createUserRequest))
			.isInstanceOf(AlreadyExistException.class)
			.hasMessage(Message.ALREADY_EXISTS_USER.getMessage());
	}

	@Test
	void 로그인_성공() {
		String password = "testPassword";
		String encryptPassword = new BCryptPasswordEncoder().encode(password);
		User user = new User("test@test.com", encryptPassword, "testuser");

		given(passwordEncoder.matches(password, encryptPassword)).willReturn(true);
		given(userRepository.findByEmail(any(String.class))).willReturn(Optional.of(user));

		Long userId = user.getId();
		String email = user.getEmail();

		userService.login(email, password);

		verify(jwtService).generateToken(userId, email);
	}

	@Test
	void 잘못된_이메일_일_때_로그인_실패() {
		given(userRepository.findByEmail(any(String.class)))
			.willThrow(new IllegalArgumentException(Message.INVALID_EMAIL_OR_PASSWORD.getMessage()));

		String email = user.getEmail();
		String password = user.getPassword();

		assertThatThrownBy(() -> userService.login(email, password))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage(Message.INVALID_EMAIL_OR_PASSWORD.getMessage());
	}

	@Test
	void 잘못된_비밀번호_일_때_로그인_실패() {
		given(userRepository.findByEmail(any(String.class))).willReturn(Optional.of(user));

		String email = user.getEmail();
		String password = "invalidPassword";

		assertThatThrownBy(() -> userService.login(email, password))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage(Message.INVALID_EMAIL_OR_PASSWORD.getMessage());
	}

	@Test
	void 로그아웃_성공() {
		User user = createUser();
		String accessToken = "dfdskjvkcldsax";

		given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
		doNothing().when(jwtService).saveLogoutAccessToken(any(String.class), any(String.class));
		doNothing().when(jwtService).deleteRefreshToken(any(String.class));

		userService.logout(user.getId(), accessToken);

		verify(jwtService).saveLogoutAccessToken(user.getEmail(), accessToken);
		verify(jwtService).deleteRefreshToken(user.getEmail());
	}

}
