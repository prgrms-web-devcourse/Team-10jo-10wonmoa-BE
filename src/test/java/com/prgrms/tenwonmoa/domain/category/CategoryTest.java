package com.prgrms.tenwonmoa.domain.category;

import static com.prgrms.tenwonmoa.domain.category.CategoryType.*;
import static org.assertj.core.api.Assertions.*;

import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayName("카테고리 도메인 테스트")
class CategoryTest {

	@ParameterizedTest
	@CsvSource({"식비", "문화생활", "마트/편의점", "주거/통신"})
	void 카테고리_생성_성공(String name) {
		Category category = new Category(name, EXPENDITURE);

		assertThat(category.getName()).isEqualTo(name);
		assertThat(category.getCategoryType()).isEqualTo(EXPENDITURE);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 이름_공백인_카테고리_생성_실패(String name) {
		assertThatIllegalArgumentException().isThrownBy(
			() -> new Category(name, EXPENDITURE));
	}

	@Test
	void 이름_길이제한_넘는_카테고리_생성_실패() {
		String name = RandomString.make(Category.MAX_NAME_LENGTH + 1);
		assertThatIllegalArgumentException().isThrownBy(
			() -> new Category(name, EXPENDITURE));
	}
}
