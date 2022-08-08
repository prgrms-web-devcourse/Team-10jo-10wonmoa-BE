package com.prgrms.tenwonmoa.domain.budget.controller.intergration;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.prgrms.tenwonmoa.common.BaseControllerIntegrationTest;
import com.prgrms.tenwonmoa.domain.budget.dto.CreateBudgetRequest;
import com.prgrms.tenwonmoa.domain.budget.repository.BudgetRepository;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.repository.CategoryRepository;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;

@DisplayName("수입 컨트롤러 통합 테스트")
public class BudgetIntegrationTest extends BaseControllerIntegrationTest {
	private static final String LOCATION_PREFIX = "/api/v1/budgets";
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserCategoryRepository userCategoryRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private BudgetRepository budgetRepository;
	private User loginUser;
	private Category category;
	private UserCategory userCategory;

	@BeforeEach
	void init() throws Exception {
		registerUserAndLogin();
		loginUser = userRepository.findByEmail("testuser@gmail.com").get();
		category = categoryRepository.save(createIncomeCategory());
		userCategory = userCategoryRepository.save(createUserCategory(loginUser, category));
	}

	@AfterEach
	void clear() {
		budgetRepository.deleteAllInBatch();
		userCategoryRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
		categoryRepository.deleteAllInBatch();
	}

	@Test
	void 예산_등록_성공() throws Exception {
		CreateBudgetRequest createBudgetRequest = new CreateBudgetRequest(
			1000L, LocalDate.now(), userCategory.getId());

		mvc.perform(post(LOCATION_PREFIX)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createBudgetRequest))
				.header(HttpHeaders.AUTHORIZATION, accessToken)
			)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("id").exists());
	}

}
