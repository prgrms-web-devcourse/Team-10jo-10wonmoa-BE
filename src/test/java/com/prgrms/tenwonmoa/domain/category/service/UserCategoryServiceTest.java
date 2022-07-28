package com.prgrms.tenwonmoa.domain.category.service;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.repository.CategoryRepository;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;

@SpringBootTest
@DisplayName("유저 카테고리 서비스 테스트")
class UserCategoryServiceTest {

	private User user;

	@Autowired
	private UserCategoryRepository userCategoryRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private UserCategoryService userCategoryService;

	@BeforeEach
	void setup() {
		user = userRepository.save(createUser());
	}

	@AfterEach
	void tearDown() {
		userCategoryRepository.deleteAll();
		userRepository.deleteAll();
		categoryRepository.deleteAll();
	}

	@Test
	void 유저카테고리_등록_성공_조회_성공() {
		//given
		String categoryType = "EXPENDITURE";
		String categoryName = "예시지출카테고리";

		//when
		Long userCategoryId = userCategoryService.register(user, categoryType, categoryName);

		//then
		UserCategory savedUserCategory = userCategoryService.getById(userCategoryId);
		Category savedCategory = savedUserCategory.getCategory();
		assertThat(savedCategory)
			.extracting(Category::getName, Category::getCategoryType)
			.isEqualTo(List.of(categoryName, CategoryType.valueOf(categoryType)));
	}

	@Test
	void 아이디로_유저카테고리조회_실패() {
		assertThatExceptionOfType(NoSuchElementException.class)
			.isThrownBy(() -> userCategoryService.getById(1L));
	}
}
