package com.prgrms.tenwonmoa.domain.category.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.prgrms.tenwonmoa.common.fixture.Fixture;
import com.prgrms.tenwonmoa.domain.user.User;

@SpringBootTest
class CategoryServiceTest {

	private final User user = Fixture.createUser();

	@Autowired
	private CategoryService service;



	@Test
	void 카테고리_등록() {
		//given
		//when
		//then
	}
}