package com.prgrms.tenwonmoa.domain.user.service;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.category.service.CreateDefaultUserCategoryService;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.dto.CreateUserRequest;
import com.prgrms.tenwonmoa.domain.user.dto.LoginUserResponse;
import com.prgrms.tenwonmoa.domain.user.jwt.TokenProvider;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;
import com.prgrms.tenwonmoa.exception.AlreadyExistException;
import com.prgrms.tenwonmoa.exception.message.Message;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	private final TokenProvider tokenProvider;

	private final CreateDefaultUserCategoryService createDefaultUserCategoryService;

	public User findById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new NoSuchElementException(Message.USER_NOT_FOUND.getMessage()));
	}

	@Transactional
	public Long createUser(CreateUserRequest createUserRequest) {
		if (userRepository.existsByEmail(createUserRequest.getEmail())) {
			throw new AlreadyExistException(Message.ALREADY_EXISTS_USER);
		}

		User savedUser = userRepository.save(createUserRequest.toEntity());
		createDefaultUserCategoryService.createDefaultUserCategory(savedUser);
		return savedUser.getId();
	}

	public LoginUserResponse login(String email, String password) {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException(Message.INVALID_EMAIL_OR_PASSWORD.getMessage()));

		if (!checkPassword(user.getPassword(), password)) {
			throw new IllegalArgumentException(Message.INVALID_EMAIL_OR_PASSWORD.getMessage());
		}

		String accessToken = tokenProvider.generateToken(user.getId(), email);
		// todo: refreshToken 추가 및 accessToken 과 함께 반환
		String refreshToken = "";

		return new LoginUserResponse(accessToken, refreshToken);
	}

	private boolean checkPassword(String originalPassword, String requestPassword) {
		// todo: PasswordEncoder 를 통한 비밀번호 암호화
		return originalPassword.equals(requestPassword);
	}

}
