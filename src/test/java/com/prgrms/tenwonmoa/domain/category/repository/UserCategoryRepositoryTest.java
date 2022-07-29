package com.prgrms.tenwonmoa.domain.category.repository;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.prgrms.tenwonmoa.common.RepositoryTest;
import com.prgrms.tenwonmoa.common.annotation.CustomDataJpaTest;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;

@CustomDataJpaTest
@DisplayName("유저카테고리 리포지토리 테스트")
class UserCategoryRepositoryTest extends RepositoryTest {

	private Category incomeCategory;

	private Category expenditureCategory;

	private User user;

	@Autowired
	private UserCategoryRepository userCategoryRepository;

	@BeforeEach
	void setup() {
		incomeCategory = save(createIncomeCategory());
		expenditureCategory = save(createExpenditureCategory());
		user = save(createUser());
	}

	@Test
	void 아이디로_유저카테고리_조회() {
		// given
		UserCategory savedUserCategory = userCategoryRepository.save(createUserCategory(user, incomeCategory));

		// when
		Optional<UserCategory> userCategoryOptional = userCategoryRepository.findById(savedUserCategory.getId());

		// then
		assertThat(userCategoryOptional).isPresent();
		UserCategory userCategory = userCategoryOptional.get();
		assertThat(savedUserCategory).isEqualTo(userCategory);
	}

	@Test
	void 유저아이디와_유저카테고리타입으로_조회() {
		// given
		userCategoryRepository.save(createUserCategory(user, incomeCategory));

		userCategoryRepository.save(createUserCategory(user, expenditureCategory));

		// when
		List<UserCategory> userCategories =
			userCategoryRepository.findByUserIdAndCategoryType(user.getId(), CategoryType.EXPENDITURE);

		// then
		assertThat(userCategories).hasSize(1);
		assertThat(userCategories.get(0).getCategoryType()).isEqualTo(CategoryType.EXPENDITURE);
	}
}
