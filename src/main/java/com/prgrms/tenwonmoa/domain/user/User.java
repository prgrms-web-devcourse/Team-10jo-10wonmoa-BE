package com.prgrms.tenwonmoa.domain.user;

import static lombok.AccessLevel.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.prgrms.tenwonmoa.domain.common.BaseEntity;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "user")
public class User extends BaseEntity {

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "username", nullable = false)
	private String username;

	public User(String email, String password, String username) {
		this.email = email;
		this.password = password;
		this.username = username;
	}
}
