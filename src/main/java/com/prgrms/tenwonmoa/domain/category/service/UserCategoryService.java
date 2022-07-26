package com.prgrms.tenwonmoa.domain.category.service;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.exception.message.Message;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserCategoryService {

	private final UserCategoryRepository userCategoryRepository;

	public UserCategory findById(Long userCategoryId) {
		return userCategoryRepository.findById(userCategoryId)
			.orElseThrow(() -> new NoSuchElementException(Message.USER_CATEGORY_NOT_FOUND.getMessage()));
	}
}
