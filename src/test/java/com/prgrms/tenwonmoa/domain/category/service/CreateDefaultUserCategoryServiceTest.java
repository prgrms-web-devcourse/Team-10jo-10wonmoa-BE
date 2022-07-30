package com.prgrms.tenwonmoa.domain.category.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.prgrms.tenwonmoa.common.fixture.Fixture;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.repository.CategoryRepository;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;

@SpringBootTest
@DisplayName("디폴트 카테고리 생성 서비스 테스트")
class CreateDefaultUserCategoryServiceTest {

	@Autowired
	private CreateDefaultUserCategoryService service;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private UserCategoryRepository userCategoryRepository;

	private User user;

	@BeforeEach
	void setup() {
		user = userRepository.save(Fixture.createUser());
	}

	@AfterEach
	void tearDown() {
		userCategoryRepository.deleteAll();
		categoryRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	void 디폴트_카테고리_생성_성공() {
		//when
		service.createDefaultUserCategory(user);

		//then
		List<UserCategory> expenditureUserCategories =
			userCategoryRepository.findByUserIdAndCategoryType(user.getId(), CategoryType.EXPENDITURE);

		List<UserCategory> incomeUserCategories =
			userCategoryRepository.findByUserIdAndCategoryType(user.getId(), CategoryType.INCOME);

		List<Category> categories = categoryRepository.findAll();

		int expenditureCategorySize = Category.DEFAULT_CATEGORY.get(CategoryType.EXPENDITURE).size();
		int incomeCategorySize = Category.DEFAULT_CATEGORY.get(CategoryType.INCOME).size();

		assertThat(expenditureUserCategories).hasSize(expenditureCategorySize);
		assertThat(incomeUserCategories).hasSize(incomeCategorySize);
		assertThat(categories).hasSize(expenditureCategorySize + incomeCategorySize);
	}
}
