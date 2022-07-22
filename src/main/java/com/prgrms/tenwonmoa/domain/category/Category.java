package com.prgrms.tenwonmoa.domain.category;

import static javax.persistence.EnumType.*;
import static lombok.AccessLevel.*;

import com.prgrms.tenwonmoa.domain.common.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
public class Category extends BaseEntity {

  @Column(nullable = false)
  private String name;

  @Column(name = "category_kind")
  @Enumerated(STRING)
  private CategoryType categoryType;

  public Category(String name, CategoryType categoryType) {
    this.name = name;
    this.categoryType = categoryType;
  }
}
