package com.prgrms.tenwonmoa.domain.category.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.common.fixture.Fixture;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.dto.service.SingleCategoryResult;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;

@SpringBootTest
@Transactional
class CategoryServiceTest {

	private final User user = Fixture.createUser();

	@Autowired
	private CategoryService service;

	@Autowired
	private UserRepository userRepository;
	// TODO : UserService 구현 시, UserService로 교체

	@Autowired
	private UserCategoryRepository userCategoryRepository;
	// TODO : UserCategoryService로 리팩토링 시, UserCategoryService로 교체

	@BeforeEach
	void setup() {
		userRepository.save(user);
	}

	@Test
	void 카테고리_등록_성공() {
		//given
		String categoryType = "EXPENDITURE";
		String categoryName = "예시지출카테고리";

		//when
		Long categoryId = service.register(user, categoryType, categoryName);

		//then
		SingleCategoryResult category = service.getById(categoryId);
		assertThat(category).extracting(
				SingleCategoryResult::getName,
				SingleCategoryResult::getType)
			.isEqualTo(List.of(categoryName, categoryType));
	}

	@Test
	void 카테고리_이름_업데이트_성공() {
		//given
		String categoryType = "EXPENDITURE";
		String categoryName = "예시지출카테고리";
		Long categoryId = service.register(user, categoryType, categoryName);

		//when
		service.updateName(user, categoryId, "업데이트된카테고리");

		//then
		SingleCategoryResult categoryResult = service.getById(categoryId);
		assertThat(categoryResult.getName()).isEqualTo("업데이트된카테고리");
	}

	@Test
	void 유저_카테고리에_존재하지_않을시_업데이트_오류() {
		//given
		String categoryType = "EXPENDITURE";
		String categoryName = "예시지출카테고리";
		Long categoryId = service.register(user, categoryType, categoryName);

		UserCategory userCategory =
			userCategoryRepository.findByUserAndCategory(user.getId(), categoryId).orElseThrow();
		userCategoryRepository.delete(userCategory);

		//when
		//then
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(
			() -> service.updateName(user, categoryId, "업데이트된카테고리")
		);
	}

	@Test
	void 카테고리_삭제_성공() {
		//given
		String categoryType = "EXPENDITURE";
		String categoryName = "예시지출카테고리";
		Long categoryId = service.register(user, categoryType, categoryName);

		//when
		service.delete(user, categoryId);

		//then
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(
			() -> service.getById(categoryId)
		);
	}

	@Test
	void 유저_카테고리에_존재하지_않을시_삭제_오류() {
		//given
		String categoryType = "EXPENDITURE";
		String categoryName = "예시지출카테고리";
		Long categoryId = service.register(user, categoryType, categoryName);

		UserCategory userCategory =
			userCategoryRepository.findByUserAndCategory(user.getId(), categoryId).orElseThrow();
		userCategoryRepository.delete(userCategory);

		//when
		//then
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(
			() -> service.delete(user, categoryId)
		);

	}
}
