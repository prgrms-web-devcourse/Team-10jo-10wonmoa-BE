package com.prgrms.tenwonmoa.domain.category.service;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
import com.prgrms.tenwonmoa.exception.UnauthorizedUserException;

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
		Long userCategoryId = userCategoryService.createUserCategory(user, categoryType, categoryName);

		//then
		UserCategory savedUserCategory = userCategoryService.findById(userCategoryId);
		Category savedCategory = savedUserCategory.getCategory();
		assertThat(savedCategory)
			.extracting(Category::getName, Category::getCategoryType)
			.isEqualTo(List.of(categoryName, CategoryType.valueOf(categoryType)));
	}

	@Test
	void 아이디로_유저카테고리조회_실패() {
		assertThatExceptionOfType(NoSuchElementException.class)
			.isThrownBy(() -> userCategoryService.findById(1L));
	}

	@Test
	void 카테고리의_이름_수정_성공() {
		//given
		String categoryType = "EXPENDITURE";
		String categoryName = "예시지출카테고리";
		Long userCategoryId = userCategoryService.createUserCategory(user, categoryType, categoryName);

		//when
		userCategoryService.updateName(user, userCategoryId, "업데이트된 카테고리 이름");

		//then
		Category category = userCategoryService.findById(userCategoryId).getCategory();
		assertThat(category.getName()).isEqualTo("업데이트된 카테고리 이름");
	}

	@Test
	@Transactional
	void 유저카테고리_이름_수정시_지출_수입도_이름수정_성공() {
		//given
		String categoryType = "EXPENDITURE";
		String categoryName = "예시지출카테고리";
		Long userCategoryId = userCategoryService.createUserCategory(user, categoryType, categoryName);
		UserCategory userCategory = userCategoryService.findById(userCategoryId);

		Expenditure savedExpenditure = expenditureRepository.save(
			new Expenditure(LocalDateTime.now(), 10000L,
				"내용", categoryName, user, userCategory)
		);

		Income savedIncome = incomeRepository.save(
			new Income(LocalDateTime.now(), 10000L,
				"내용", categoryName, user, userCategory)
		);

		//when
		userCategoryService.updateName(user, userCategoryId, "업데이트 카테고리");

		//then
		UserCategory updatedUserCategory = userCategoryService.findById(userCategoryId);
		Category updatedCategory = updatedUserCategory.getCategory();
		assertThat(updatedCategory.getName()).isEqualTo("업데이트 카테고리");

		Expenditure updatedExpenditure = expenditureRepository.findById(
			savedExpenditure.getId()).orElseThrow();
		assertThat(updatedExpenditure.getCategoryName()).isEqualTo("업데이트 카테고리");

		Income updatedIncome = incomeRepository.findById(
			savedIncome.getId()).orElseThrow();
		assertThat(updatedIncome.getCategoryName()).isEqualTo("업데이트 카테고리");
	}

	@Test
	void 유저가_권한이_없어_카테고리_수정_실패() {
		//given
		String categoryType = "EXPENDITURE";
		String categoryName = "예시지출카테고리";
		Long userCategoryId = userCategoryService.createUserCategory(user, categoryType, categoryName);

		//when
		//then
		assertThatExceptionOfType(UnauthorizedUserException.class).isThrownBy(
			() -> userCategoryService.updateName(otherUser, userCategoryId, "업데이트된 카테고리 이름")
		);

	}

	@Test
	void 삭제된_카테고리는_카테고리_이름_수정_실패() {
		//given
		String categoryType = "EXPENDITURE";
		String categoryName = "예시지출카테고리";
		Long userCategoryId = userCategoryService.createUserCategory(user, categoryType, categoryName);
		userCategoryService.deleteUserCategory(user, userCategoryId);

		//when
		//then
		assertThatNullPointerException().isThrownBy(
			() -> userCategoryService.updateName(user, userCategoryId, "업데이트된 카테고리 이름")
		);

	}

	@Test
	void 유저카테고리_삭제_성공() {
		//given
		String categoryType = "EXPENDITURE";
		String categoryName = "예시지출카테고리";
		Long userCategoryId = userCategoryService.createUserCategory(user, categoryType, categoryName);

		//when
		userCategoryService.deleteUserCategory(user, userCategoryId);

		//then
		UserCategory userCategory = userCategoryService.findById(userCategoryId);
		assertThat(userCategory.getCategory()).isNull();
	}

	@Test
	void 유저가_권한이_없어_카테고리_삭제_실패() {
		//given
		String categoryType = "EXPENDITURE";
		String categoryName = "예시지출카테고리";
		Long userCategoryId = userCategoryService.createUserCategory(user, categoryType, categoryName);

		//when
		//then
		assertThatExceptionOfType(UnauthorizedUserException.class).isThrownBy(
			() -> userCategoryService.deleteUserCategory(otherUser, userCategoryId));
	}

	@Test
	void 이미_삭제가_된_유저카테고리는_삭제_실패() {
		//given
		String categoryType = "EXPENDITURE";
		String categoryName = "예시지출카테고리";
		Long userCategoryId = userCategoryService.createUserCategory(user, categoryType, categoryName);

		//when
		userCategoryService.deleteUserCategory(user, userCategoryId);
		//then
		assertThatNullPointerException()
			.isThrownBy(() -> userCategoryService.deleteUserCategory(user, userCategoryId));
	}
}
