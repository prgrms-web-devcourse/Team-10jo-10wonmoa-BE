package com.prgrms.tenwonmoa.domain.accountbook.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
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
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;

@DisplayName("수입 Repository 테스트")
class IncomeRepositoryTest extends RepositoryFixture {

	@Autowired
	private IncomeRepository incomeRepository;

	private User user;

	private Category category;

	private UserCategory userCategory;

	@BeforeEach
	void setup() {
		user = saveUser();
		category = saveCategory();
		userCategory = save(new UserCategory(user, category));
	}

	@Test
	void 유저아이디와_수입아이디로_조회_성공() {
		// given
		Income income = saveIncome();
		User user = income.getUser();

		// when
		Optional<Income> findIncome = incomeRepository.findByIdAndUserId(income.getId(), user.getId());

		// then
		assertThat(findIncome).isPresent();
		Income getIncome = findIncome.get();
		assertAll(
			() -> assertThat(getIncome.getId()).isEqualTo(income.getId()),
			() -> assertThat(getIncome.getUser().getId()).isEqualTo(user.getId())
		);
	}

	@Test
	void 로그인한아이디가_다른계정의_수입을_조회할수없다() {
		// given
		Income loginIncome = saveIncome();
		Income otherIncome = saveIncome();

		User loginUser = loginIncome.getUser();

		// when
		Optional<Income> findIncome = incomeRepository.findByIdAndUserId(otherIncome.getId(), loginUser.getId());

		// then
		assertThat(findIncome).isEmpty();
	}

	@Test
	void 해당하는_유저카테고리_아이디를_가진_수입의_유저카테고리를_null_로_업데이트() {
		//given
		Income income = new Income(
			LocalDate.now(), 10000L, "내용", category.getName(), user, userCategory);

		Income income2 = new Income(
			LocalDate.now(), 10000L, "내용", category.getName(), user, userCategory);

		Income income3 = new Income(
			LocalDate.now(), 10000L, "내용", category.getName(), user, userCategory);

		incomeRepository.saveAll(List.of(income, income2, income3));

		//when
		incomeRepository.updateUserCategoryAsNull(userCategory.getId());

		//then
		List<UserCategory> userCategories = incomeRepository.findAll()
			.stream()
			.map(Income::getUserCategory)
			.collect(Collectors.toList());
		assertThat(userCategories).containsExactly(null, null, null);
	}

}
