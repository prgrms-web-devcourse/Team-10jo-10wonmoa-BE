package com.prgrms.tenwonmoa.domain.accountbook.repository;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static com.prgrms.tenwonmoa.domain.accountbook.AccountBookConst.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.prgrms.tenwonmoa.common.fixture.RepositoryFixture;
import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;

@DisplayName("수입 Repository 테스트")
class IncomeRepositoryTest extends RepositoryFixture {

	private List<Long> allUserCategoryIds;

	@Autowired
	private IncomeRepository incomeRepository;

	private User user;

	private Category category;

	private UserCategory userCategory;

	private UserCategory userCategory2;

	@BeforeEach
	void setup() {
		user = save(createUser());

		category = save(new Category("월급", CategoryType.INCOME));
		Category category2 = save(new Category("용돈", CategoryType.INCOME));

		userCategory = save(createUserCategory(user, category));
		userCategory2 = save(createUserCategory(user, category2));

		allUserCategoryIds = new ArrayList<>(List.of(userCategory.getId(), userCategory2.getId()));
	}

	@Test
	void 해당하는_유저카테고리_아이디를_가진_수입의_유저카테고리를_null_로_업데이트() {
		//given
		save(createIncome(userCategory));
		save(createIncome(userCategory));
		save(createIncome(userCategory));

		//when
		incomeRepository.updateUserCategoryAsNull(userCategory.getId());

		//then
		List<UserCategory> userCategories = incomeRepository.findAll()
			.stream()
			.map(Income::getUserCategory)
			.collect(Collectors.toList());
		assertThat(userCategories).containsExactly(null, null, null);
	}

	@Test
	void 해당하는_유저카테고리를_가지는_수입의_카테고리_이름_필드_업데이트() {
		//given
		save(createIncome(userCategory));
		save(createIncome(userCategory));
		save(createIncome(userCategory));

		//when
		// Income의 categoryName 필드 접근 위해 가지고 있는 카테고리를 null 로 변경
		userCategory.updateCategoryAsNull();
		merge(userCategory);

		incomeRepository.updateCategoryName(userCategory.getId(), "업데이트된카테고리이름");

		//then
		List<Income> incomes = incomeRepository.findAll();

		assertThat(incomes).extracting(Income::getCategoryName)
			.containsExactlyInAnyOrder("업데이트된카테고리이름", "업데이트된카테고리이름", "업데이트된카테고리이름");

	}

	@Test
	void 수입정보_삭제_성공() {
		Income income = saveIncome();
		Long incomeId = income.getId();

		incomeRepository.deleteById(incomeId);

		Optional<Income> findIncome = incomeRepository.findById(incomeId);
		assertThat(findIncome).isEmpty();
	}

	@Test
	void 금액조건을_만족하는_수입의_합계_조회() {
		//given
		saveIncome(defaultTime, 1000L, defaultContent, category.getName(), user,
			userCategory);
		saveIncome(defaultTime, 10000L, defaultContent, category.getName(), user,
			userCategory);
		saveIncome(defaultTime, 20000L, defaultContent, category.getName(), user,
			userCategory);
		saveIncome(defaultTime, 50000L, defaultContent, category.getName(), user,
			userCategory);

		//when
		Long sum = incomeRepository.getSumOfIncome(
			1000L, 50000L, LEFT_MOST_REGISTER_DATE.atTime(LocalTime.MIN),
			RIGHT_MOST_REGISTER_DATE.atTime(LocalTime.MAX), defaultContent, allUserCategoryIds, user.getId());

		//then
		assertThat(sum).isEqualTo(81000L);
	}

	@Test
	void 내용_조건을_만족하는_수입의_합계_조회() {
		//given
		saveIncome(defaultTime, defaultAmount, "점심식사",
			category.getName(), user, userCategory);

		saveIncome(defaultTime, defaultAmount, "영화",
			category.getName(), user, userCategory);

		saveIncome(defaultTime, defaultAmount, "영화관람",
			category.getName(), user, userCategory);

		saveIncome(defaultTime, defaultAmount, "문화 영화 관람",
			category.getName(), user, userCategory);

		//when
		Long sum = incomeRepository.getSumOfIncome(
			AMOUNT_MIN, AMOUNT_MAX, LEFT_MOST_REGISTER_DATE.atTime(LocalTime.MIN),
			RIGHT_MOST_REGISTER_DATE.atTime(LocalTime.MAX), "영화", allUserCategoryIds,
			user.getId());

		//then
		assertThat(sum).isEqualTo(3 * defaultAmount);
	}

	@Test
	void 등록날짜_조건을_만족하는_수입의_합계_조회() {
		//given
		LocalDateTime latestDate = LocalDateTime.now().minusDays(1);
		LocalDateTime secondDate = LocalDateTime.now().minusDays(2);
		LocalDateTime thirdDate = LocalDateTime.now().minusDays(5);
		LocalDateTime fourthDate = LocalDateTime.now().minusDays(10);

		saveIncome(latestDate, defaultAmount, defaultContent, category.getName(), user,
			userCategory);
		saveIncome(secondDate, defaultAmount, defaultContent, category.getName(), user,
			userCategory);
		saveIncome(thirdDate, defaultAmount, defaultContent, category.getName(), user,
			userCategory);
		saveIncome(fourthDate, defaultAmount, defaultContent, category.getName(), user,
			userCategory);

		//when
		Long sum = incomeRepository.getSumOfIncome(
			AMOUNT_MIN, AMOUNT_MAX, LocalDate.now().minusDays(6).atTime(LocalTime.MIN),
			LocalDate.now().atTime(LocalTime.MAX), defaultContent, allUserCategoryIds, user.getId());

		//then
		assertThat(sum).isEqualTo(3 * defaultAmount);
	}

	@Test
	void 카테고리_조건을_만족하는_수입_합계_조회() {
		//given

		// userCategory
		saveIncome(defaultTime, defaultAmount, defaultContent, userCategory.getCategoryName(), user,
			userCategory);

		// userCategory2
		saveIncome(defaultTime, defaultAmount, defaultContent, userCategory2.getCategoryName(), user,
			userCategory2);

		saveIncome(defaultTime, defaultAmount, defaultContent, userCategory2.getCategoryName(), user,
			userCategory2);

		//when
		Long userCategorySum = incomeRepository.getSumOfIncome(
			AMOUNT_MIN, AMOUNT_MAX, LEFT_MOST_REGISTER_DATE.atTime(LocalTime.MIN),
			RIGHT_MOST_REGISTER_DATE.atTime(LocalTime.MAX), defaultContent,
			List.of(userCategory.getId()), user.getId());

		Long userCategorySum2 = incomeRepository.getSumOfIncome(
			AMOUNT_MIN, AMOUNT_MAX, LEFT_MOST_REGISTER_DATE.atTime(LocalTime.MIN),
			RIGHT_MOST_REGISTER_DATE.atTime(LocalTime.MAX), defaultContent,
			List.of(userCategory2.getId()), user.getId());

		//then
		assertThat(userCategorySum).isEqualTo(defaultAmount);
		assertThat(userCategorySum2).isEqualTo(2 * defaultAmount);
	}

	@Test
	void 유저_아이디_조건을_만족하는_수입의_합계_조회() {
		//given
		User otherUser = save(createAnotherUser());
		UserCategory otherUserCategory = save(createUserCategory(otherUser, category));

		allUserCategoryIds.add(otherUserCategory.getId());

		// user
		saveIncome(defaultTime, 10000L, defaultContent,
			userCategory.getCategoryName(), user, userCategory);

		// other user
		saveIncome(defaultTime, 20000L, defaultContent,
			otherUserCategory.getCategoryName(), otherUser, otherUserCategory);

		//when
		Long sum = incomeRepository.getSumOfIncome(
			AMOUNT_MIN, AMOUNT_MAX, LEFT_MOST_REGISTER_DATE.atTime(LocalTime.MIN),
			RIGHT_MOST_REGISTER_DATE.atTime(LocalTime.MAX), defaultContent,
			allUserCategoryIds, user.getId());

		Long otherUserSum = incomeRepository.getSumOfIncome(
			AMOUNT_MIN, AMOUNT_MAX, LEFT_MOST_REGISTER_DATE.atTime(LocalTime.MIN),
			RIGHT_MOST_REGISTER_DATE.atTime(LocalTime.MAX), defaultContent,
			allUserCategoryIds, otherUser.getId());

		//then
		assertThat(sum).isEqualTo(10000L);
		assertThat(otherUserSum).isEqualTo(20000L);
	}

}
