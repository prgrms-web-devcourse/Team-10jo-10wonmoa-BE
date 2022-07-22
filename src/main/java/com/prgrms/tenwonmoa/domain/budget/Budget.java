package com.prgrms.tenwonmoa.domain.budget;

import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.prgrms.tenwonmoa.domain.common.BaseEntity;
import com.prgrms.tenwonmoa.domain.user.User;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "budget")
public class Budget extends BaseEntity {

	@Column(name = "amount")
	private Long amount;

	@Column(name = "start_time")
	private LocalDate startTime;

	@Column(name = "end_time")
	private LocalDate endTime;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	public Budget(Long amount, LocalDate startTime, LocalDate endTime, User user) {
		this.amount = amount;
		this.startTime = startTime;
		this.endTime = endTime;
		this.user = user;
	}
}
