package com.prgrms.tenwonmoa.domain.category.service;

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

import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.exception.message.Message;

@DisplayName("유저 카테고리 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class UserCategoryServiceTest {
	@Mock
	private UserCategoryRepository userCategoryRepository;

	@InjectMocks
	private UserCategoryService userCategoryService;

	private final UserCategory userCategory = createUserCategory();

	@Test
	void 아이디로_유저카테고리조회_성공() {
		given(userCategoryRepository.findById(userCategory.getId())).willReturn(Optional.of(userCategory));

		UserCategory findUserCategory = userCategoryService.findById(userCategory.getId());
		assertAll(
			() -> assertThat(findUserCategory.getId()).isEqualTo(userCategory.getId()),
			() -> verify(userCategoryRepository).findById(userCategory.getId())
		);
	}

	@Test
	void 아이디로_유저카테고리조회_실패() {
		given(userCategoryRepository.findById(any())).willThrow(
			new NoSuchElementException(Message.USER_CATEGORY_NOT_FOUND.getMessage()));

		assertThatThrownBy(() -> userCategoryService.findById(any()))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage(Message.USER_CATEGORY_NOT_FOUND.getMessage());
	}
}
