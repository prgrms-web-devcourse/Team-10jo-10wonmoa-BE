package com.prgrms.tenwonmoa.domain.category.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.prgrms.tenwonmoa.common.fixture.Fixture;
import com.prgrms.tenwonmoa.domain.category.controller.dto.CategoryResponse.SingleCategoryResponse;
import com.prgrms.tenwonmoa.domain.user.User;

@SpringBootTest
class CategoryServiceTest {

	private final User user = Fixture.createUser();

	@Autowired
	private CategoryService service;

	@Test
	void 카테고리_등록() {
		//given
		String categoryType = "EXPENDITURE";
		String categoryName = "예시지출카테고리";

		//when
		SingleCategoryResponse categoryResponse = service.register(user, categoryType, categoryName);

		//then
		assertThat(categoryResponse)
			.extracting(SingleCategoryResponse::getType, SingleCategoryResponse::getName)
			.isEqualTo(List.of(categoryType, categoryName));
	}
}