package com.prgrms.tenwonmoa.domain.common.deploy.intergration;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProfileControllerIntergrationTest {
	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void profile_인증없이_호출가능() {
		String expected = "default";

		ResponseEntity<String> response = restTemplate.getForEntity("/profile", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo(expected);
	}
}
