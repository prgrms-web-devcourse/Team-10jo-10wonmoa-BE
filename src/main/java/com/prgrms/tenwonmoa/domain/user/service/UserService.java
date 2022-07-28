package com.prgrms.tenwonmoa.domain.user.service;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.dto.CreateUserRequest;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;
import com.prgrms.tenwonmoa.exception.AlreadyExistException;
import com.prgrms.tenwonmoa.exception.message.Message;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public User findById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new NoSuchElementException(Message.USER_NOT_FOUND.getMessage()));
	}

	public Long createUser(CreateUserRequest createUserRequest) {
		if (userRepository.existsByEmail(createUserRequest.getEmail())) {
			throw new AlreadyExistException(Message.ALREADY_EXISTS_USER);
		}

		User savedUser = userRepository.save(createUserRequest.toEntity());
		return savedUser.getId();
	}


}
