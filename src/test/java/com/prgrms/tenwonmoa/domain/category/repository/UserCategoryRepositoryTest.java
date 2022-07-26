package com.prgrms.tenwonmoa.domain.category.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.common.annotation.CustomDataJpaTest;
import com.prgrms.tenwonmoa.common.fixture.Fixture;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;

@CustomDataJpaTest
@Transactional
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
	void 유저와_카테고리로_유저카테고리_조회() {
		//given
		//when
		userCategoryRepository.save(new UserCategory(user, category));

		//then
		Optional<UserCategory> userCategoryOptional =
			userCategoryRepository.findByUserAndCategory(user.getId(), category.getId());

		assertThat(userCategoryOptional).isPresent();
		assertThat(userCategoryOptional.get())
			.extracting(UserCategory::getUser, UserCategory::getCategory)
			.isEqualTo(List.of(user, category));
	}
}
