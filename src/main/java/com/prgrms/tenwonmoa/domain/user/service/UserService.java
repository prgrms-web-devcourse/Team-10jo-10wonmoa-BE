package com.prgrms.tenwonmoa.domain.user.service;

import java.util.NoSuchElementException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.category.service.CreateDefaultUserCategoryService;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.dto.CreateUserRequest;
import com.prgrms.tenwonmoa.domain.user.dto.TokenResponse;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;
import com.prgrms.tenwonmoa.domain.user.security.jwt.service.JwtService;
import com.prgrms.tenwonmoa.exception.AlreadyExistException;
import com.prgrms.tenwonmoa.exception.message.Message;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final CreateDefaultUserCategoryService createDefaultUserCategoryService;
	private final JwtService jwtService;
	private final UserDeleteService userDeleteService;

	public User findById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new NoSuchElementException(Message.USER_NOT_FOUND.getMessage()));
	}

	@Transactional
	public Long createUser(CreateUserRequest createUserRequest) {
		if (userRepository.existsByEmail(createUserRequest.getEmail())) {
			throw new AlreadyExistException(Message.ALREADY_EXISTS_USER);
		}

		User savedUser = userRepository.save(createUserRequest.toEntity(passwordEncoder));
		createDefaultUserCategoryService.createDefaultUserCategory(savedUser);
		return savedUser.getId();
	}

	@Transactional
	public TokenResponse login(String email, String password) {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException(Message.INVALID_EMAIL_OR_PASSWORD.getMessage()));

		checkPassword(user.getPassword(), password);

		return jwtService.generateToken(user.getId(), email);
	}

	public void checkPassword(String encodedPassword, String requestPassword) {
		if (!passwordEncoder.matches(requestPassword, encodedPassword)) {
			throw new IllegalArgumentException(Message.INVALID_EMAIL_OR_PASSWORD.getMessage());
		}
	}

	@Transactional
	public TokenResponse refresh(String accessToken, String refreshToken) {
		return jwtService.refreshToken(accessToken, refreshToken);
	}

	@Transactional
	public void logout(Long userId, String accessToken) {
		User user = this.findById(userId);

		jwtService.saveLogoutAccessToken(user.getEmail(), accessToken);
		jwtService.deleteRefreshToken(user.getEmail());
	}

	@Transactional
	public void deleteUser(Long userId) {
		User user = this.findById(userId);

		userDeleteService.deleteUserData(userId);
		jwtService.deleteRefreshToken(user.getEmail());
	}

}
