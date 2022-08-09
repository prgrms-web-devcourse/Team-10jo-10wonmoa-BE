package com.prgrms.tenwonmoa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import com.prgrms.tenwonmoa.domain.user.security.oauth2.OAuth2AuthenticationSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return new CustomAuthenticationEntryPoint();
	}

	@Bean
	public OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler() {
		return new OAuth2AuthenticationSuccessHandler();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.cors()
				.and()
			.authorizeRequests()
				.antMatchers("/api/v1/users", "/api/v1/users/login", "/api/v1/users/refresh", "/docs/**",
					"api/v1/oauth2/**", "/login/**")
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
					.and()
				.redirectionEndpoint()
					.baseUri("/login/oauth2/code/google")    // redirect default 값
					.and()
				.successHandler(oauth2AuthenticationSuccessHandler());    // 로그인 성공 처리하는 handler

		// filter 등록
		http.addFilterAfter(
			jwtAuthenticationFilter,
			CorsFilter.class
		);
	}
}
