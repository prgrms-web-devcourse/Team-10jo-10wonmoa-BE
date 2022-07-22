package com.prgrms.tenwonmoa.domain.category;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.prgrms.tenwonmoa.domain.common.BaseEntity;
import com.prgrms.tenwonmoa.domain.user.User;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
public class UserCategory extends BaseEntity {

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = LAZY, optional = false)
  @JoinColumn(name = "category_id")
  private Category category;

  public UserCategory(User user, Category category) {
    this.user = user;
    this.category = category;
  }
}
