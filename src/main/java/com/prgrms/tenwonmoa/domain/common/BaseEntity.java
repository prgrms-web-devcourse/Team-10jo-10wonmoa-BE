package com.prgrms.tenwonmoa.domain.common;

import static javax.persistence.GenerationType.*;

import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@EqualsAndHashCode
public class BaseEntity {

	@Id
	@GeneratedValue(strategy = AUTO)
	private Long id;
}
