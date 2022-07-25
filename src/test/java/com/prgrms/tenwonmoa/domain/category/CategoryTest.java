package com.prgrms.tenwonmoa.domain.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class CategoryTest {

  @ParameterizedTest
  @CsvSource({"식비", "문화생활", "마트/편의점", "주거/통신"})
  void 카테고리_생성_성공(String name) {
    Category category = new Category(name, CategoryType.EXPENDITURE);

    assertThat(category).extracting(Category::getName, Category::getCategoryType)
        .isEqualTo(List.of(name, CategoryType.EXPENDITURE));
  }

  @ParameterizedTest
  @NullAndEmptySource
  void 카테고리_이름은_공백일수_없다(String name) {
    assertThatIllegalArgumentException().isThrownBy(
        () -> new Category(name, CategoryType.EXPENDITURE));
  }

  @ParameterizedTest
  @CsvSource({"카테고리이름은20자이상일수없다아직도조금더남음", "백엔드데브코스2기최종프로젝트백엔드프로젝트같이진행함"})
  void 카테고리_이름은_20자_이상일수_없다(String name) {
    assertThatIllegalArgumentException().isThrownBy(
        () -> new Category(name, CategoryType.EXPENDITURE));
  }
}