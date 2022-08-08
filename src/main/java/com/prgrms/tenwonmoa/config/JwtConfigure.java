package com.prgrms.tenwonmoa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtConfigure {
	private String header;
	private String issuer;
	private String clientSecret;
	private int expirySecondsMillis;
	private int refreshExpirySecondsMillis;
}
