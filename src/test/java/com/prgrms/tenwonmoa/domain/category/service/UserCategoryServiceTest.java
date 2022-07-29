package com.prgrms.tenwonmoa.domain.category.service;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.repository.ExpenditureRepository;
import com.prgrms.tenwonmoa.domain.accountbook.repository.IncomeRepository;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;

@SpringBootTest
@DisplayName("유저 카테고리 서비스 테스트")
class UserCategoryServiceTest {

	private User user;

	private User otherUser;

	@Autowired
	private UserCategoryRepository userCategoryRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ExpenditureRepository expenditureRepository;

	@Autowired
	private IncomeRepository incomeRepository;

	@Autowired
	private UserCategoryService userCategoryService;

	@BeforeEach
	void setup() {
		user = userRepository.save(createUser());
		otherUser = userRepository.save(createAnotherUser());
	}

	@AfterEach
	void tearDown() {
		userCategoryRepository.deleteAll();
		expenditureRepository.deleteAll();
		incomeRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	void 유저카테고리_등록_성공_조회_성공() {
		//given
		String categoryType = "EXPENDITURE";
		String categoryName = "예시지출카테고리";

		//when
		Long userCategoryId = userCategoryService.register(user, categoryType, categoryName);

		//then
		UserCategory savedUserCategory = userCategoryService.getById(userCategoryId);
		Category savedCategory = savedUserCategory.getCategory();
		assertThat(savedCategory)
			.extracting(Category::getName, Category::getCategoryType)
			.isEqualTo(List.of(categoryName, CategoryType.valueOf(categoryType)));
	}

	@Test
	void 아이디로_유저카테고리조회_실패() {
		assertThatExceptionOfType(NoSuchElementException.class)
			.isThrownBy(() -> userCategoryService.getById(1L));
	}

	@Test
	void 카테고리의_이름_수정_성공() {
		//given
		String categoryType = "EXPENDITURE";
		String categoryName = "예시지출카테고리";
		Long userCategoryId = userCategoryService.register(user, categoryType, categoryName);

		//when
		userCategoryService.updateName(user, userCategoryId, "업데이트된 카테고리 이름");

		//then
		Category category = userCategoryService.getById(userCategoryId).getCategory();
		assertThat(category.getName()).isEqualTo("업데이트된 카테고리 이름");
	}

	@Test
	void 유저가_권한이_없어_카테고리_수정_실패() {
		//given
		String categoryType = "EXPENDITURE";
		String categoryName = "예시지출카테고리";
		Long userCategoryId = userCategoryService.register(user, categoryType, categoryName);

		//when
		//then
		assertThatIllegalStateException().isThrownBy(
			() -> userCategoryService.updateName(otherUser, userCategoryId, "업데이트된 카테고리 이름")
		);
	}

	@Test
	void 유저카테고리_삭제_성공() {
		//given
		String categoryType = "EXPENDITURE";
		String categoryName = "예시지출카테고리";
		Long userCategoryId = userCategoryService.register(user, categoryType, categoryName);

		//when
		userCategoryService.delete(user, userCategoryId);

		//then
		assertThatExceptionOfType(NoSuchElementException.class)
			.isThrownBy(() -> userCategoryService.getById(userCategoryId));
	}

	@Test
	void 유저가_권한이_없어_카테고리_삭제_실패() {
		//given
		String categoryType = "EXPENDITURE";
		String categoryName = "예시지출카테고리";
		Long userCategoryId = userCategoryService.register(user, categoryType, categoryName);

		//when
		//then
		assertThatIllegalStateException().isThrownBy(
			() -> userCategoryService.delete(otherUser, userCategoryId));
	}

	@Test
	void 유저카테고리를_갖고있는_지출과_수입이_있어도_유저카테고리_삭제_성공() {
		//given
		String categoryType = "EXPENDITURE";
		String categoryName = "예시지출카테고리";
		Long userCategoryId = userCategoryService.register(user, categoryType, categoryName);
		UserCategory userCategory = userCategoryService.getById(userCategoryId);

		Expenditure savedExpenditure = expenditureRepository.save(
<<<<<<< HEAD
			new Expenditure(LocalDateTime.now(), 10000L, "내용", "식비", user, userCategory));
		Income savedIncome = incomeRepository.save(new Income(LocalDate.now(), 10000L, "내용", "식비", user, userCategory));
=======
			new Expenditure(LocalDate.now(), 10000L, "내용", "식비", user, userCategory));
		Income savedIncome = incomeRepository.save(
			new Income(LocalDateTime.now(), 10000L, "내용", "식비", user, userCategory));
>>>>>>> develop

		//when
		userCategoryService.delete(user, userCategoryId);

		//then
		assertThatExceptionOfType(NoSuchElementException.class)
			.isThrownBy(() -> userCategoryService.getById(userCategoryId));

		Optional<Expenditure> expenditureOptional = expenditureRepository.findById(savedExpenditure.getId());
		Optional<Income> incomeOptional = incomeRepository.findById(savedIncome.getId());

		assertThat(expenditureOptional).isPresent();
		assertThat(expenditureOptional.get().getUserCategory()).isNull();

		assertThat(incomeOptional).isPresent();
		assertThat(incomeOptional.get().getUserCategory()).isNull();

	}

	@Test
	void 유저카테고리_존재하지_않을시_삭제_실패() {
		//when
		//then
		assertThatExceptionOfType(NoSuchElementException.class)
			.isThrownBy(() -> userCategoryService.delete(user, 1L));
	}
}
