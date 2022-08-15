package com.prgrms.tenwonmoa.domain.category;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import com.prgrms.tenwonmoa.domain.user.User;

@DisplayName("유저카테고리 도메인 테스트")
class UserCategoryTest {

	private final User user = createUser();

	private final Category category = new Category("식비", CategoryType.EXPENDITURE);

	@Test
	void 유저_카테고리_생성_성공() {
		UserCategory userCategory = new UserCategory(user, category);

		assertThat(userCategory.getUser()).isEqualTo(user);
		assertThat(userCategory.getCategory()).isEqualTo(category);
	}

	@ParameterizedTest
	@NullSource
	void 유저가_null_유저_카테고리_생성_실패(User user) {
		assertThatIllegalArgumentException().isThrownBy(() -> new UserCategory(user, category));
	}

	@ParameterizedTest
	@NullSource
	void 카테고리가_null_유저_카테고리_생성_실패(Category category) {
		assertThatIllegalArgumentException().isThrownBy(() -> new UserCategory(user, category));
	}

	@Test
	void 유저카테고리_이름_조회_성공() {
		//given
		Category category = new Category("식비", CategoryType.EXPENDITURE);
		UserCategory userCategory = new UserCategory(user, category);

		//when
		//then
		assertThat(userCategory.getCategoryName()).isEqualTo("식비");
	}

	@Test
	void 유저카테고리_카테고리_타입_조회_성공() {
		//given
		Category category = new Category("식비", CategoryType.EXPENDITURE);
		UserCategory userCategory = new UserCategory(user, category);

		//when
		//then
		assertThat(userCategory.getCategoryTypeName()).isEqualTo(CategoryType.EXPENDITURE.name());
	}

	@Test
	void 카테고리_삭제_성공() {
		//given
		Category category = new Category("식비", CategoryType.EXPENDITURE);
		UserCategory userCategory = new UserCategory(user, category);

		//when
		userCategory.updateCategoryAsNull();
		//then
		assertThat(userCategory.getCategory()).isNull();
	}
}
