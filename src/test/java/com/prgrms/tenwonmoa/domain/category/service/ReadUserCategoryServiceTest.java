package com.prgrms.tenwonmoa.domain.category.service;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.prgrms.tenwonmoa.domain.category.dto.service.ReadCategoryResult;
import com.prgrms.tenwonmoa.domain.category.dto.service.ReadCategoryResult.SingleCategoryResult;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;

@SpringBootTest
@DisplayName("카테고리 조회 서비스 테스트")
class ReadUserCategoryServiceTest {

	@Autowired
	private ReadUserCategoryService readUserCategoryService;

	@Autowired
	private UserCategoryService userCategoryService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserCategoryRepository userCategoryRepository;

	private User user;

	@BeforeEach
	void setup() {
		user = userRepository.save(createUser());
	}

	@AfterEach
	void tearDown() {
		userCategoryRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	void 유저카테고리_조회_성공() {
		//given
		userCategoryService.register(user, "EXPENDITURE", "식비");
		userCategoryService.register(user, "EXPENDITURE", "교통비");
		userCategoryService.register(user, "EXPENDITURE", "문화생활");
		userCategoryService.register(user, "INCOME", "월급");
		userCategoryService.register(user, "INCOME", "투자");

		//when
		ReadCategoryResult expenditureCategories =
			readUserCategoryService.getUserCategories(user.getId(), "EXPENDITURE");

		ReadCategoryResult incomeCategories =
			readUserCategoryService.getUserCategories(user.getId(), "INCOME");

		//then
		assertThat(expenditureCategories.getCategories())
			.extracting(SingleCategoryResult::getName)
			.containsExactlyInAnyOrder("식비", "교통비", "문화생활");

		assertThat(incomeCategories.getCategories())
			.extracting(SingleCategoryResult::getName)
			.containsExactlyInAnyOrder("월급", "투자");
	}

	@Test
	void 잘못된_카테고리_타입_입력시_조회실패() {
		//given
		userCategoryService.register(user, "EXPENDITURE", "식비");
		userCategoryService.register(user, "INCOME", "월급");

		//when
		//then
		assertThatIllegalArgumentException().isThrownBy(
			() -> readUserCategoryService.getUserCategories(user.getId(), "NOT_VALID")
		);
	}
}