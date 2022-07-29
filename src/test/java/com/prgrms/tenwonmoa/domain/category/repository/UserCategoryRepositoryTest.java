package com.prgrms.tenwonmoa.domain.category.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.prgrms.tenwonmoa.common.annotation.CustomDataJpaTest;
import com.prgrms.tenwonmoa.common.fixture.Fixture;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;

@CustomDataJpaTest
@DisplayName("유저카테고리 리포지토리 테스트")
class UserCategoryRepositoryTest {

	private Category category;

	private User user;

	@Autowired
	private UserCategoryRepository userCategoryRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@BeforeEach
	void setup() {
		category = categoryRepository.save(new Category("식비", CategoryType.EXPENDITURE));
		user = userRepository.save(Fixture.createUser());
	}

	@Test
	void 아이디로_유저카테고리_조회() {
		// given
		UserCategory savedUserCategory = userCategoryRepository.save(new UserCategory(user, category));
		// when
		Optional<UserCategory> userCategoryOptional = userCategoryRepository.findById(savedUserCategory.getId());
		// then
		assertThat(userCategoryOptional).isPresent();
		UserCategory userCategory = userCategoryOptional.get();
		assertThat(savedUserCategory).isEqualTo(userCategory);
	}
}
