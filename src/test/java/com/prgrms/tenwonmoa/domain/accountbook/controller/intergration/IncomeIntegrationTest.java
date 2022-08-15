package com.prgrms.tenwonmoa.domain.accountbook.controller.intergration;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static com.prgrms.tenwonmoa.domain.category.CategoryType.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import net.bytebuddy.utility.RandomString;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.tenwonmoa.common.BaseControllerIntegrationTest;
import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.dto.income.CreateIncomeRequest;
import com.prgrms.tenwonmoa.domain.accountbook.dto.income.UpdateIncomeRequest;
import com.prgrms.tenwonmoa.domain.accountbook.repository.ExpenditureRepository;
import com.prgrms.tenwonmoa.domain.accountbook.repository.IncomeRepository;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.repository.CategoryRepository;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;

@DisplayName("수입 컨트롤러 통합 테스트")
class IncomeIntegrationTest extends BaseControllerIntegrationTest {
	private static final String LOCATION_PREFIX = "/api/v1/incomes/";

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserCategoryRepository userCategoryRepository;
	@Autowired
	private IncomeRepository incomeRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ExpenditureRepository expenditureRepository;

	private User loginUser;
	private Category category;
	private UserCategory userCategory;
	private Income income;

	@BeforeEach
	void init() throws Exception {
		registerUserAndLogin();
		loginUser = userRepository.findByEmail("testuser@gmail.com").get();
		category = categoryRepository.save(createIncomeCategory());
		userCategory = userCategoryRepository.save(createUserCategory(loginUser, category));
		income = incomeRepository.save(createIncome(userCategory));
	}

	@AfterEach
	void clear() {
		incomeRepository.deleteAllInBatch();
		expenditureRepository.deleteAllInBatch();
		userCategoryRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
		categoryRepository.deleteAllInBatch();
	}

	@Test
	void 수입_등록_Valid_등록일() throws Exception {
		CreateIncomeRequest request = new CreateIncomeRequest(null, 2000L, "content2", userCategory.getId());

		validateCreateRequest(request, "등록일을 채워주세요");
	}

	@Test
	void 수입_등록_Valid_금액() throws Exception {
		CreateIncomeRequest requestMin = new CreateIncomeRequest(LocalDateTime.now(), -1L, "content2",
			userCategory.getId());

		CreateIncomeRequest requestMax = new CreateIncomeRequest(LocalDateTime.now(), 1_000_000_000_001L, "content2",
			userCategory.getId());

		validateCreateRequest(requestMin, "최소값은 0입니다");
		validateCreateRequest(requestMax, "최대값은 1조입니다");
	}

	@Test
	void 수입_등록_Valid_내용() throws Exception {
		CreateIncomeRequest request = new CreateIncomeRequest(LocalDateTime.now(), 1000L, RandomString.make(51),
			userCategory.getId());

		validateCreateRequest(request, "내용의 최대 길이는 50입니다");
	}

	@Test
	void 수입_등록_Valid_카테고리_ID() throws Exception {
		CreateIncomeRequest request = new CreateIncomeRequest(LocalDateTime.now(), 1000L, "content", null);

		validateCreateRequest(request, "유저 카테고리 아이디를 채워주세요");
	}

	@Test
	void 수입_등록_성공() throws Exception {
		CreateIncomeRequest request = new CreateIncomeRequest(LocalDateTime.now(), 2000L, "content2",
			userCategory.getId());

		mvc.perform(post(LOCATION_PREFIX).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header(HttpHeaders.AUTHORIZATION, accessToken))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("id").exists());
	}

	@Test
	void 수입_등록_다른유저의_카테고리_등록시도_실패() throws Exception {
		UserCategory otherUserCategory = getOtherUserCategory();

		CreateIncomeRequest request = new CreateIncomeRequest(LocalDateTime.now(), 2000L, "content2",
			otherUserCategory.getId());

		mvc.perform(post(LOCATION_PREFIX).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header(HttpHeaders.AUTHORIZATION, accessToken))
			.andExpect(status().isForbidden());
	}

	@Test
	void 수입_상세조회_성공() throws Exception {
		mvc.perform(get(LOCATION_PREFIX + "/{incomeId}", income.getId()).header(HttpHeaders.AUTHORIZATION, accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("id").value(income.getId()))
			.andExpect(jsonPath("amount").value(income.getAmount()))
			.andExpect(jsonPath("content").value(income.getContent()))
			.andExpect(jsonPath("userCategoryId").value(income.getUserCategory().getId()))
			.andExpect(jsonPath("categoryName").value(userCategory.getCategoryName()));
	}

	@Test
	void 수입_수정_성공() throws Exception {
		Category otherCategory = categoryRepository.save(new Category("다른 카테고리", INCOME));
		UserCategory otherUserCategory = userCategoryRepository.save(createUserCategory(loginUser, otherCategory));

		UpdateIncomeRequest updateIncomeRequest = new UpdateIncomeRequest(LocalDateTime.now(), 2000L, "updateContent",
			otherUserCategory.getId());

		mvc.perform(put(LOCATION_PREFIX + "/{incomeId}", income.getId())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateIncomeRequest))
			.header(HttpHeaders.AUTHORIZATION, accessToken)).andExpect(status().isNoContent());

		Optional<Income> findIncome = incomeRepository.findById(income.getId());

		assertThat(findIncome).isPresent();

		Income getIncome = findIncome.get();
		assertThat(getIncome.getAmount()).isEqualTo(updateIncomeRequest.getAmount());
		assertThat(getIncome.getContent()).isEqualTo(updateIncomeRequest.getContent());
		assertThat(getIncome.getUserCategory().getId()).isEqualTo(updateIncomeRequest.getUserCategoryId());
	}

	@Test
	void 수입_수정_다른유저카테고리_실패() throws Exception {
		UserCategory otherUserCategory = getOtherUserCategory();

		UpdateIncomeRequest updateIncomeRequest = new UpdateIncomeRequest(LocalDateTime.now(), 2000L, "updateContent",
			otherUserCategory.getId());

		mvc.perform(put(LOCATION_PREFIX + "/{incomeId}", income.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateIncomeRequest))
				.header(HttpHeaders.AUTHORIZATION, accessToken))
			.andExpect(status().isForbidden());
	}

	@Test
	void 수입에서_지출로_변경() throws Exception {
		Category otherCategory = categoryRepository.save(new Category("다른 카테고리", EXPENDITURE));
		UserCategory otherUserCategory = userCategoryRepository.save(createUserCategory(loginUser, otherCategory));

		UpdateIncomeRequest updateIncomeRequest = new UpdateIncomeRequest(LocalDateTime.now(), 2000L, "updateContent",
			otherUserCategory.getId());

		mvc.perform(put(LOCATION_PREFIX + "/{incomeId}", income.getId()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateIncomeRequest))
			.header(HttpHeaders.AUTHORIZATION, accessToken)).andExpect(status().isNoContent());

		Optional<Income> findIncome = incomeRepository.findById(income.getId());
		assertThat(findIncome).isEmpty();
	}

	private UserCategory getOtherUserCategory() {
		User otherUser = userRepository.saveAndFlush(new User("other@email.com", "other1234", "other"));
		Category category = categoryRepository.save(new Category("other", INCOME));
		UserCategory otherUserCategory = userCategoryRepository.save(new UserCategory(otherUser, category));
		return otherUserCategory;
	}

	private void validateCreateRequest(CreateIncomeRequest request, String expectMessage) throws Exception {
		mvc.perform(post(LOCATION_PREFIX).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header(HttpHeaders.AUTHORIZATION, accessToken))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("messages").value(expectMessage));
	}
}
