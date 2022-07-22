package com.prgrms.tenwonmoa.domain.common;

import static javax.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@Getter
public class BaseEntity {

  @Id
  @GeneratedValue(strategy = AUTO)
  private Long id;
}
