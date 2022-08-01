package com.prgrms.tenwonmoa.common.annotation;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
	@Override
	public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();

		Long principal = 1L; // userId;
		Authentication auth = new UsernamePasswordAuthenticationToken(principal, customUser.password(),
			AuthorityUtils.NO_AUTHORITIES);
		context.setAuthentication(auth);
		return context;
	}
}
