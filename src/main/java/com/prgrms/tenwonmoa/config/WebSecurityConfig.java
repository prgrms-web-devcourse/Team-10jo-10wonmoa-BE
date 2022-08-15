package com.prgrms.tenwonmoa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.CorsFilter;

import com.prgrms.tenwonmoa.domain.user.security.jwt.filter.CustomAuthenticationEntryPoint;
import com.prgrms.tenwonmoa.domain.user.security.jwt.filter.JwtAuthenticationFilter;
import com.prgrms.tenwonmoa.domain.user.security.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.prgrms.tenwonmoa.domain.user.security.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.prgrms.tenwonmoa.domain.user.security.oauth2.repository.HttpCookieOAuth2AuthorizationRequestRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
	private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return new CustomAuthenticationEntryPoint();
	}

	@Bean
	public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
		return new HttpCookieOAuth2AuthorizationRequestRepository();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.cors()
				.and()
			.authorizeRequests()
				.antMatchers("/docs/**")
					.permitAll()
				.antMatchers(HttpMethod.POST, "/api/v1/users", "/api/v1/users/login",  "/api/v1/users/refresh")
					.permitAll()
				.anyRequest().authenticated()
				.and()
			.csrf()        // disable 하지 않으면 unauthorized
				.disable()
			.httpBasic()    // 토큰을 사용하므로 basic 인증 disable
				.disable()
			.sessionManagement()    // 토큰을 사용하므로 stateless
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
			.exceptionHandling()
				.authenticationEntryPoint(authenticationEntryPoint())
				.and()
			.oauth2Login()
				.authorizationEndpoint()
					.baseUri("/api/v1/oauth2/authorize")        // 구글 로그인 인증 하는 URL
					.authorizationRequestRepository(cookieAuthorizationRequestRepository())	// 인증 요청 정보를 저장하는 레포지토리
					.and()
				.redirectionEndpoint()
					.baseUri("/login/oauth2/code/google")    // redirect default 값
					.and()
				.successHandler(oAuth2AuthenticationSuccessHandler)
				.failureHandler(oAuth2AuthenticationFailureHandler);

		// filter 등록
		http.addFilterAfter(
			jwtAuthenticationFilter,
			CorsFilter.class
		);
	}
}
