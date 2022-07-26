package com.prgrms.tenwonmoa.domain.category.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.common.fixture.Fixture;
import com.prgrms.tenwonmoa.domain.category.dto.service.SingleCategoryResult;
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

	@BeforeEach
	void setup() {
		userRepository.save(user);
	}

	@Test
	void 카테고리_등록() {
		//given
		String categoryType = "EXPENDITURE";
		String categoryName = "예시지출카테고리";

		//when
		Long categoryId = service.register(user, categoryType, categoryName);
		SingleCategoryResult category = service.getById(categoryId);

		//then
		assertThat(category).extracting(
				SingleCategoryResult::getName,
				SingleCategoryResult::getType)
			.isEqualTo(List.of(categoryName, categoryType));
	}

}
