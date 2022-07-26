package com.prgrms.tenwonmoa.domain.accountbook;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.prgrms.tenwonmoa.domain.accountbook.dto.UpdateExpenditureRequest;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;

@DisplayName("지출(Expenditure) domain 테스트")
class ExpenditureTest {

	private final LocalDate date = LocalDate.now();

	private final Long amount = 10000L;

	private final String content = "돈까스";

	private final String categoryName = "식비";

	// user 추후 수정 필요
	private final User user = new User("jungki111@gmail,com", "password1234!", "개발자");

	private final UserCategory userCategory = new UserCategory(user, new Category("식비", CategoryType.EXPENDITURE));

	@Nested
	@DisplayName("Expenditure 생성 중에서")
	class ExpenditureConstruct {

		@Test
		public void registerDate가_null일_때() {
			assertThatThrownBy(
				() -> new Expenditure(
					null,
					amount,
					content,
					categoryName,
					user,
					userCategory
				)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		public void amount가_0원_이하_일_때() {
			assertThatThrownBy(
				() -> new Expenditure(
					date,
					0L,
					content,
					categoryName,
					user,
					userCategory
				)
			).isInstanceOf(IllegalArgumentException.class);

			assertThatThrownBy(
				() -> new Expenditure(
					date,
					-1L,
					content,
					categoryName,
					user,
					userCategory
				)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		public void amount가_1조를_넘을때() {
			assertThatThrownBy(
				() -> new Expenditure(
					date,
					1000000000001L,
					content,
					categoryName,
					user,
					userCategory
				)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		public void categoryName이_null일때() {
			assertThatThrownBy(
				() -> new Expenditure(
					date,
					amount,
					content,
					null,
					user,
					userCategory
				)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		public void categoryName이_공백일때() {
			assertThatThrownBy(
				() -> new Expenditure(
					date,
					amount,
					content,
					"  ",
					user,
					userCategory
				)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		public void user가_null일때() {
			assertThatThrownBy(
				() -> new Expenditure(
					date,
					amount,
					content,
					categoryName,
					null,
					userCategory
				)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		public void userCategory가_null일때() {
			assertThatThrownBy(
				() -> new Expenditure(
					date,
					amount,
					content,
					categoryName,
					user,
					null
				)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		public void 성공적으로_생성_할_수_있다() {
			Expenditure expenditure = new Expenditure(
				date,
				amount,
				content,
				categoryName,
				user,
				userCategory
			);

			assertThat(expenditure.getContent()).isEqualTo(content);
		}
	}

	@Nested
	@DisplayName("Expenditure 수정 시")
	class ExpenditureUpdate {

		private final Expenditure expenditure = new Expenditure(
			date,
			amount,
			content,
			categoryName,
			user,
			userCategory
		);

		private final UpdateExpenditureRequest request = new UpdateExpenditureRequest(
			LocalDate.now(),
			20000L,
			"그런게 있어",
			2L
		);

		private final Category category = new Category("분류분류", CategoryType.EXPENDITURE);

		private final UserCategory otherUserCategory = new UserCategory(user, category);

		@Test
		public void 성공적으로_변경한다() {
			expenditure.update(otherUserCategory, request);
			assertThat(expenditure.getAmount()).isEqualTo(request.getAmount());
			assertThat(expenditure.getContent()).isEqualTo(request.getContent());
			assertThat(expenditure.getUserCategory().getCategory().getName())
				.isEqualTo(otherUserCategory.getCategory().getName());
		}

	}

}
