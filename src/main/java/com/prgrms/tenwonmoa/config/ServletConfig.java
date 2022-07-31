package com.prgrms.tenwonmoa.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class ServletConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
			.allowedOrigins("http://localhost:3000", "https://team-10jo-10wonmoa-fe.vercel.app")
			.allowedMethods("*")
			.allowedHeaders("*");
	}
}
