package com.prgrms.tenwonmoa.domain.common.deploy;

import java.util.Arrays;
import java.util.List;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProfileController {
	private static final List<String> DEV_PROFILES = Arrays.asList("dev", "dev2");
	private static final String DEFAULT_PROFILE = "default";
	private final Environment env;

	@GetMapping("/profile")
	public String profile() {
		List<String> profiles = Arrays.asList(env.getActiveProfiles());
		String defaultProfile = profiles.isEmpty() ? DEFAULT_PROFILE : profiles.get(0);

		return profiles.stream().filter(DEV_PROFILES::contains).findAny().orElse(defaultProfile);
	}
}
