package com.prgrms.tenwonmoa.domain.budget;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static com.prgrms.tenwonmoa.domain.accountbook.AccountBookConst.*;
import static com.prgrms.tenwonmoa.exception.message.Message.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.YearMonth;

import org.junit.jupiter.api.Test;

import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;

class BudgetTest {
	private User user = createUser();
	private Category category = createExpenditureCategory();
	private UserCategory userCategory = createUserCategory(user, category);

	private final YearMonth now = YearMonth.now();

	@Test
	void 예산_생성_성공() {
		Budget budget = new Budget(1000L, now, user, userCategory);

		assertAll(() -> assertThat(budget.getUser()).isEqualTo(user),
			() -> assertThat(budget.getAmount()).isEqualTo(1000L));
	}

	@Test
	void 예산금액은_최대크기를_넘을수없다() {
		assertThatThrownBy(() -> new Budget(AMOUNT_MAX + 1, now, user, userCategory)).isInstanceOf(
			IllegalArgumentException.class).hasMessageContaining(INVALID_AMOUNT_ERR_MSG.getMessage());
	}

	@Test
	void 예산금액금은_0원이하_등록할수없다() {
		assertThatThrownBy(() -> new Budget(0L, now, user, userCategory))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining(INVALID_AMOUNT_ERR_MSG.getMessage());
	}

	@Test
	void nullable_false_필드테스트() {
		assertAll(
			() -> assertThatThrownBy(() -> new Budget(null, now, user, userCategory))
				.isInstanceOf(IllegalArgumentException.class),
			() -> assertThatThrownBy(() -> new Budget(1000L, null, user, userCategory))
				.isInstanceOf(IllegalArgumentException.class),
			() -> assertThatThrownBy(() -> new Budget(1000L, now, null, userCategory))
				.isInstanceOf(IllegalArgumentException.class),
			() -> assertThatThrownBy(() -> new Budget(1000L, now, user, null))
				.isInstanceOf(IllegalArgumentException.class)
		);
	}
}
