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

import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;
import com.prgrms.tenwonmoa.exception.message.Message;

@DisplayName("유저 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	@Mock
	private UserRepository userRepository;

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
}
