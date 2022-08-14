package com.prgrms.tenwonmoa.domain.accountbook.controller.intergration;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static com.prgrms.tenwonmoa.domain.category.CategoryType.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.tenwonmoa.common.BaseControllerIntegrationTest;
import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.accountbook.dto.expenditure.CreateExpenditureRequest;
import com.prgrms.tenwonmoa.domain.accountbook.dto.expenditure.UpdateExpenditureRequest;
import com.prgrms.tenwonmoa.domain.accountbook.repository.ExpenditureRepository;
import com.prgrms.tenwonmoa.domain.accountbook.repository.IncomeRepository;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.repository.CategoryRepository;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;

@DisplayName("Expenditure 통합테스트")
public class ExpenditureIntergrationTest extends BaseControllerIntegrationTest {
	private static final String BASE_URL = "/api/v1/expenditures";

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

	private Expenditure expenditure;

	@BeforeEach
	void setup() throws Exception {
		registerUserAndLogin();
		loginUser = userRepository.findByEmail("testuser@gmail.com").get();
		category = categoryRepository.save(createExpenditureCategory());
		userCategory = userCategoryRepository.save(createUserCategory(loginUser, category));
		expenditure = expenditureRepository.save(createExpenditure(userCategory));
	}

	@AfterEach
	void teardown() {
		incomeRepository.deleteAllInBatch();
		expenditureRepository.deleteAllInBatch();
		userCategoryRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
		categoryRepository.deleteAllInBatch();
	}

	@Nested
	@DisplayName("지출 등록 API 호출 중")
	class DescribeOfExpenditurePost {
		@Test
		public void 성공적으로_지출을_등록한다() throws Exception {
			CreateExpenditureRequest request = new CreateExpenditureRequest(
				LocalDateTime.now(),
				1000L,
				"content",
				userCategory.getId()
			);

			mvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
					.header(HttpHeaders.AUTHORIZATION, accessToken))
				.andExpect(status().is(HttpStatus.CREATED.value()))
				.andExpect(jsonPath("id").exists());
		}
	}

	@Nested
	@DisplayName("지출 조회 API 호출 중")
	class DescribeOfExpenditureGet {
		@Test
		public void 성공적으로_지출을_조회한다() throws Exception {
			mvc.perform(get(BASE_URL + "/{expenditureId}", expenditure.getId())
					.header(HttpHeaders.AUTHORIZATION, accessToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(expenditure.getId()))
				.andExpect(jsonPath("amount").value(expenditure.getAmount()))
				.andExpect(jsonPath("content").value(expenditure.getContent()))
				.andExpect(jsonPath("userCategoryId").value(expenditure.getUserCategory().getId()))
				.andExpect(jsonPath("categoryName").value(userCategory.getCategoryName()));
		}
	}

	@Nested
	@DisplayName("지출 수정 API 호출 증")
	class DescribeOfExpenditurePut {

		@Test
		public void 성공적으로_지출을_수정한다() throws Exception {
			Category otherCategory = categoryRepository.save(new Category("다른 카테고리", EXPENDITURE));

			UserCategory otherUserCategory = userCategoryRepository.save(
				createUserCategory(loginUser, otherCategory));

			UpdateExpenditureRequest request = new UpdateExpenditureRequest(
				LocalDateTime.now(),
				20000L,
				"수정",
				otherUserCategory.getId()
			);

			mvc.perform(put(BASE_URL + "/{expenditureId}", expenditure.getId())
				.header(HttpHeaders.AUTHORIZATION, accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
			).andExpect(status().isNoContent());

			Optional<Expenditure> maybeExpenditure = expenditureRepository.findById(expenditure.getId());

			assertThat(maybeExpenditure).isPresent();

			Expenditure updatedExpenditure = maybeExpenditure.get();

			assertThat(updatedExpenditure.getAmount()).isEqualTo(request.getAmount());
			assertThat(updatedExpenditure.getContent()).isEqualTo(request.getContent());
			assertThat(updatedExpenditure.getUserCategory().getId()).isEqualTo(request.getUserCategoryId());
		}
	}

	@Nested
	@DisplayName("지출 삭제 API 호출 중")
	class DescribeOfExpenditureDelete {
		@Test
		public void 성공적으로_지출을_삭제한다() throws Exception {
			mvc.perform(delete(BASE_URL + "/{expenditureId}", expenditure.getId())
				.header(HttpHeaders.AUTHORIZATION, accessToken)
				.contentType(MediaType.APPLICATION_JSON)
			).andExpect(status().isNoContent());

			Optional<Expenditure> maybeExpenditure = expenditureRepository.findById(expenditure.getId());

			assertThat(maybeExpenditure).isEmpty();
		}
	}

}
