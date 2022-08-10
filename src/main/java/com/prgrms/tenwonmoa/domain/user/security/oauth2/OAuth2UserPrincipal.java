package com.prgrms.tenwonmoa.domain.user.security.oauth2;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuth2UserPrincipal implements OAuth2User {

	private Long id;
	private String email;
	private String name;

	@Override
	public <A> A getAttribute(String name) {
		return OAuth2User.super.getAttribute(name);
	}

	@Override	// OAuth2User implements를 위해 강제화, 넘겨줄 속성이 id, email만 있으면 되서 사용 안함
	public Map<String, Object> getAttributes() {
		return null;
	}

	@Override	// OAuth2User implements를 위해 강제화, 우리는 user의 role이 없어서 사용 안함
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}
}
