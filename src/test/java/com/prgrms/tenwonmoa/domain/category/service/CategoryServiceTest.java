package com.prgrms.tenwonmoa.domain.category.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.repository.CategoryRepository;

@SpringBootTest
@DisplayName("카테고리 서비스 테스트")
class CategoryServiceTest {

	@Autowired
	private CategoryService service;

	@Autowired
	private CategoryRepository categoryRepository;

	@AfterEach
	void tearDown() {
		categoryRepository.deleteAll();
	}

	@Test
	void 카테고리_등록_성공() {
		//given
		String categoryType = "EXPENDITURE";
		String categoryName = "예시지출카테고리";

		//when
		Category category = service.register(categoryType, categoryName);

		//then
		assertThat(category).extracting(
				Category::getName,
				Category::getCategoryType)
			.isEqualTo(List.of(categoryName, CategoryType.valueOf(categoryType)));
	}

	// @Test
	// void 카테고리_삭제_성공() {
	// 	//given
	// 	String categoryType = "EXPENDITURE";
	// 	String categoryName = "예시지출카테고리";
	// 	Category category = service.register(categoryType, categoryName);
	//
	// 	//when
	// 	service.delete(user, category.getId());
	//
	// 	//then
	// 	assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(
	// 		() -> service.getById(category.getId())
	// 	);
	// }
	//
	// @Test
	// void 카테고리_존재하지않을시_삭제_실패() {
	// 	//given
	// 	//when
	// 	//then
	// 	assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(
	// 		() -> service.delete(user, 0L)
	// 	);
	// }
	//
	// @Test
	// void 유저_카테고리에_존재하지_않을시_삭제_실패() {
	// 	//given
	// 	String categoryType = "EXPENDITURE";
	// 	String categoryName = "예시지출카테고리";
	// 	Category category = service.register(categoryType, categoryName);
	//
	// 	UserCategory userCategory =
	// 		userCategoryRepository.findByUserAndCategory(user.getId(), category.getId()).orElseThrow();
	// 	userCategoryRepository.delete(userCategory);
	//
	// 	//when
	// 	//then
	// 	assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(
	// 		() -> service.delete(user, category.getId())
	// 	);
	//
	// }
}
