package com.prgrms.tenwonmoa.domain.category;

import static com.google.common.base.Preconditions.checkArgument;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.prgrms.tenwonmoa.domain.common.BaseEntity;
import com.prgrms.tenwonmoa.domain.user.User;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "user_category")
@Getter
public class UserCategory extends BaseEntity {

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = LAZY, optional = false)
  @JoinColumn(name = "category_id")
  private Category category;

  public UserCategory(User user, Category category) {
    checkArgument(category != null);

    this.user = user;
    this.category = category;
  }
}
