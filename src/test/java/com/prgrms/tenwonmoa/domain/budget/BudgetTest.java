package com.prgrms.tenwonmoa.domain.budget;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static com.prgrms.tenwonmoa.domain.accountbook.AccountBookConst.*;
import static com.prgrms.tenwonmoa.exception.message.Message.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;

class BudgetTest {
	private User user = createUser();
	private Category category = createExpenditureCategory();
	private UserCategory userCategory = createUserCategory(user, category);

	@Test
	void 예산_생성_성공() {
		Budget budget = new Budget(1000L, LocalDate.now(), user, userCategory);

		assertAll(() -> assertThat(budget.getUser()).isEqualTo(user),
			() -> assertThat(budget.getAmount()).isEqualTo(1000L));
	}

	@Test
	void 예산금액은_최대크기를_넘을수없다() {
		assertThatThrownBy(() -> new Budget(AMOUNT_MAX + 1, LocalDate.now(), user, userCategory)).isInstanceOf(
			IllegalArgumentException.class).hasMessageContaining(INVALID_AMOUNT_ERR_MSG.getMessage());
	}

	@Test
	void 예산금액금은_0원이하_등록할수없다() {
		assertThatThrownBy(() -> new Budget(0L, LocalDate.now(), user, userCategory))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining(INVALID_AMOUNT_ERR_MSG.getMessage());
	}

	@Test
	void 유저카테고리_수정() {
		Budget budget = new Budget(1000L, LocalDate.now(), user, userCategory);
		UserCategory userCategory2 = new UserCategory(user, new Category("c2", CategoryType.EXPENDITURE));
		budget.changeUserCategory(userCategory2);

		assertThat(budget.getUserCategory()).isEqualTo(userCategory2);
	}

	@Test
	void nullable_false_필드테스트() {
		assertAll(
			() -> assertThatThrownBy(() -> new Budget(null, LocalDate.now(), user, userCategory))
				.isInstanceOf(IllegalArgumentException.class),
			() -> assertThatThrownBy(() -> new Budget(1000L, null, user, userCategory))
				.isInstanceOf(IllegalArgumentException.class),
			() -> assertThatThrownBy(() -> new Budget(1000L, LocalDate.now(), null, userCategory))
				.isInstanceOf(IllegalArgumentException.class),
			() -> assertThatThrownBy(() -> new Budget(1000L, LocalDate.now(), user, null))
				.isInstanceOf(IllegalArgumentException.class)
		);
	}

}
