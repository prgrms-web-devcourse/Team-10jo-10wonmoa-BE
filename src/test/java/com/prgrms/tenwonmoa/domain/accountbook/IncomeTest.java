package com.prgrms.tenwonmoa.domain.accountbook;

import static com.prgrms.tenwonmoa.domain.accountbook.AccountBookConst.*;
import static com.prgrms.tenwonmoa.exception.message.Message.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.bytebuddy.utility.RandomString;

import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;

@DisplayName("수입 도메인 테스트")
class IncomeTest {
	private final Category category = new Category("현금", CategoryType.INCOME);
	private final User user = new User("user@email.com", "password", "user1");
	private final UserCategory userCategory = new UserCategory(user, category);

	@Test
	void 수입_생성_성공() {
		Income income = new Income(1000L, null, category.getName(), user, userCategory);

		assertAll(
			() -> assertThat(income.getUser()).isEqualTo(user),
			() -> assertThat(income.getUsercategory()).isEqualTo(userCategory)
		);
	}

	@Test
	void 수입금액은_최대크기를_넘을수없다() {
		assertThatThrownBy(() -> new Income(AMOUNT_MAX + 1, null, category.getName(), user, userCategory))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining(INVALID_AMOUNT_ERR_MSG.getMessage());
	}

	@Test
	void 수입금은_0원이하_등록할수없다() {
		assertThatThrownBy(() -> new Income(0L, null, category.getName(), user, userCategory))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining(INVALID_AMOUNT_ERR_MSG.getMessage());
	}

	@Test
	void 수입의_내용은_최대50자() {
		String content = RandomString.make(CONTENT_MAX + 1);
		assertThatThrownBy(() -> new Income(1000L, content, category.getName(), user, userCategory))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining(INVALID_CONTENT_ERR_MSG.getMessage());
	}

	@Test
	void 카테고리이름은_최대_20자() {
		String categoryName = RandomString.make(Category.MAX_NAME_LENGTH + 1);
		assertThatThrownBy(() -> new Income(1000L, null, categoryName, user, userCategory))
			.isInstanceOf(IllegalArgumentException.class);
	}
}
