package com.prgrms.tenwonmoa.domain.user.security.oauth2.service;

import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.category.service.CreateDefaultUserCategoryService;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;
import com.prgrms.tenwonmoa.domain.user.security.oauth2.OAuth2UserPrincipal;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuth2UserDetailsService extends DefaultOAuth2UserService {

	private static final String DEFAULT_OAUTH_PASSWORD = "oauthuser";
	private final UserRepository userRepository;
	private final CreateDefaultUserCategoryService createDefaultUserCategoryService;

	@Transactional
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		String email = oAuth2User.getAttribute("email");
		String name = oAuth2User.getAttribute("name");

		Long userId = saveSocialUser(email, name);

		OAuth2UserPrincipal oAuth2UserPrincipal = new OAuth2UserPrincipal(
			userId,
			email,
			name
		);

		return oAuth2UserPrincipal;
	}

	private Long saveSocialUser(String email, String name) {
		Optional<User> findUser = userRepository.findByEmail(email);
		if (findUser.isPresent()) {
			return findUser.get().getId();
		}

		User socialUser = new User(email, DEFAULT_OAUTH_PASSWORD, name, true);
		User savedSocialUser = userRepository.save(socialUser);
		createDefaultUserCategoryService.createDefaultUserCategory(savedSocialUser);

		return savedSocialUser.getId();
	}
}
