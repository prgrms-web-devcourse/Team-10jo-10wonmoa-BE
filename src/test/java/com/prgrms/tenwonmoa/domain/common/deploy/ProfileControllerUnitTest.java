package com.prgrms.tenwonmoa.domain.common.deploy;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

class ProfileControllerUnitTest {

	@Test
	void dev_profile_조회() {
		// given
		String expectedProfile = "dev";
		MockEnvironment env = new MockEnvironment();
		env.addActiveProfile(expectedProfile);
		env.addActiveProfile("other1");
		env.addActiveProfile("other2");

		ProfileController controller = new ProfileController(env);

		// when
		String profile = controller.profile();

		// then
		assertThat(profile).isEqualTo(expectedProfile);
	}

	@Test
	void dev_profile_없으면_첫번째조회() {
		// given
		String expectedProfile = "other1";
		MockEnvironment env = new MockEnvironment();
		env.addActiveProfile(expectedProfile);
		env.addActiveProfile("other2");

		ProfileController controller = new ProfileController(env);

		// when
		String profile = controller.profile();

		// then
		assertThat(profile).isEqualTo(expectedProfile);
	}

	@Test
	void active_profile_없으면_default_조회() {
		// given
		String expectedProfile = "default";
		MockEnvironment env = new MockEnvironment();

		ProfileController controller = new ProfileController(env);

		// when
		String profile = controller.profile();

		// then
		assertThat(profile).isEqualTo(expectedProfile);
	}
}
