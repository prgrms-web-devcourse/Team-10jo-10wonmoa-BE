package com.prgrms.tenwonmoa.domain.budget.controller.intergration;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static com.prgrms.tenwonmoa.domain.category.CategoryType.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.YearMonth;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.prgrms.tenwonmoa.common.BaseControllerIntegrationTest;
import com.prgrms.tenwonmoa.domain.budget.Budget;
import com.prgrms.tenwonmoa.domain.budget.dto.CreateOrUpdateBudgetRequest;
import com.prgrms.tenwonmoa.domain.budget.repository.BudgetRepository;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.repository.CategoryRepository;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;

@DisplayName("예산 컨트롤러 통합 테스트")
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

	private YearMonth now = YearMonth.now();

	@BeforeEach
	void init() throws Exception {
		registerUserAndLogin();
		loginUser = userRepository.findByEmail("testuser@gmail.com").get();
		category = categoryRepository.save(createExpenditureCategory());
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
		CreateOrUpdateBudgetRequest createOrUpdateBudgetRequest = new CreateOrUpdateBudgetRequest(
			1000L, now, userCategory.getId());

		mvc.perform(put(LOCATION_PREFIX)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createOrUpdateBudgetRequest))
				.header(HttpHeaders.AUTHORIZATION, accessToken)
			)
			.andExpect(status().isNoContent());

		Optional<Budget> findBudget = budgetRepository.findByUserCategoryIdAndRegisterDate(userCategory.getId(), now);
		assertThat(findBudget).isPresent();
	}

	@Test
	void 예산_등록_수정으로_처리되는_경우() throws Exception {
		budgetRepository.saveAndFlush(new Budget(1000L, now, loginUser, userCategory));

		CreateOrUpdateBudgetRequest createOrUpdateBudgetRequest = new CreateOrUpdateBudgetRequest(
			2000L, now, userCategory.getId());

		mvc.perform(put(LOCATION_PREFIX)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createOrUpdateBudgetRequest))
				.header(HttpHeaders.AUTHORIZATION, accessToken)
			)
			.andExpect(status().isNoContent());

		Optional<Budget> findBudget = budgetRepository.findByUserCategoryIdAndRegisterDate(userCategory.getId(), now);
		assertThat(findBudget).isPresent();
		Budget budget = findBudget.get();
		assertThat(budget.getAmount()).isEqualTo(2000L);
	}

	@Test
	void 예산_등록_잘못된_등록일포맷요청() throws Exception {
		ObjectNode createBudgetRequest = objectMapper.createObjectNode();
		createBudgetRequest.put("amount", "1000");
		createBudgetRequest.put("userCategoryId", userCategory.getId());

		validateCreateRequest(createBudgetRequest, "2022-13");
		validateCreateRequest(createBudgetRequest, "2022-00");
		validateCreateRequest(createBudgetRequest, "202211");
		validateCreateRequest(createBudgetRequest, "20220701");
		validateCreateRequest(createBudgetRequest, "2022-07-01");
	}

	@Test
	void 예산_월별_조회_성공() throws Exception {
		budgetRepository.save(new Budget(1000L, now, loginUser, userCategory));

		mvc.perform(get(LOCATION_PREFIX)
				.param("registerDate", now.toString())
				.header(HttpHeaders.AUTHORIZATION, accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("budgets").exists())
			.andExpect(jsonPath("budgets[0].id").exists())
			.andExpect(jsonPath("budgets[0].categoryName").exists())
			.andExpect(jsonPath("budgets[0].amount").exists());
	}

	@Test
	void 예산_월별_조회_잘못된_등록일포맷요청() throws Exception {
		validateFindRequest("2022-13");
		validateFindRequest("2022-00");
		validateFindRequest("202211");
		validateFindRequest("20220701");
		validateFindRequest("2022-07-01");
		validateFindRequest("2022-7");
	}

	private void validateFindRequest(String registerDate) throws Exception {
		mvc.perform(put(LOCATION_PREFIX)
				.param("registerDate", now.toString())
				.header(HttpHeaders.AUTHORIZATION, accessToken))
			.andExpect(status().isBadRequest());
	}

	private void validateCreateRequest(ObjectNode createBudgetRequest, String registerDate) throws Exception {
		createBudgetRequest.put("registerDate", registerDate);

		mvc.perform(put(LOCATION_PREFIX)
				.content(createBudgetRequest.toString())
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, accessToken)
			)
			.andExpect(status().isBadRequest());
	}

	@Disabled
	@CsvSource({"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"})
	@ParameterizedTest(name = "{0}월 테스트")
	void 예산_월별_조회_성공_모든월_돌려보기(int month) throws Exception {
		mvc.perform(get(LOCATION_PREFIX)
			.param("registerDate", YearMonth.of(2022, month).toString())
			.header(HttpHeaders.AUTHORIZATION, accessToken)).andExpect(status().isOk());
	}
}
