package com.prgrms.tenwonmoa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ServletConfig implements WebMvcConfigurer {

	public static final String SWAGGER_UI_HOST = "http://3.39.184.232:8082";

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
			.allowCredentials(true)
			.allowedOrigins("http://localhost:3000", "https://team-10jo-10wonmoa-fe.vercel.app")
			.allowedMethods("*")
			.allowedHeaders("*");

		registry.addMapping("/**")
			.allowCredentials(true)
			.allowedOrigins(SWAGGER_UI_HOST)
			.allowedMethods("*")
			.allowedHeaders("*");
	}
}
