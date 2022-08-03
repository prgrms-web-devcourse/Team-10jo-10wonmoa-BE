package com.prgrms.tenwonmoa.domain.user.security.jwt;

import static lombok.AccessLevel.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.prgrms.tenwonmoa.domain.common.BaseEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "refresh_token")
@Getter
public class RefreshToken extends BaseEntity {

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "token", nullable = false, unique = true)
	private String token;

	public RefreshToken(String email, String token) {
		this.email = email;
		this.token = token;
	}

}
