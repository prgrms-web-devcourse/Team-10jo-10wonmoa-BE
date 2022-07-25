package com.prgrms.tenwonmoa.domain.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.prgrms.tenwonmoa.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

class UserCategoryTest {

  private User user;

  private Category category;

  @BeforeEach
  void setup() {
    user = new User("test@gmail.com", "123456789", "testuser");
    category = new Category("식비", CategoryType.EXPENDITURE);
  }

  @Test
  void 유저_카테고리_생성_성공() {
    UserCategory userCategory = new UserCategory(user, category);

    assertThat(userCategory.getUser()).isEqualTo(user);
    assertThat(userCategory.getCategory()).isEqualTo(category);
  }

  @ParameterizedTest
  @NullSource
  void 유저가_null_유저_카테고리_생성_성공(User user) {
    UserCategory userCategory = new UserCategory(user, category);

    assertThat(userCategory.getUser()).isEqualTo(user);
    assertThat(userCategory.getCategory()).isEqualTo(category);
  }

  @ParameterizedTest
  @NullSource
  void 카테고리가_null_유저_카테고리_생성_실패(Category category) {
    assertThatIllegalArgumentException().isThrownBy(() -> new UserCategory(user, category));
  }
}