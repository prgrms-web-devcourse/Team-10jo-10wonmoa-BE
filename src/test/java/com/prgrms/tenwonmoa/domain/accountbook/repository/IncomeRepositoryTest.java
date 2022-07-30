package com.prgrms.tenwonmoa.domain.accountbook.repository;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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

	private UserCategory userCategory;

	private UserCategory otherUserCategory;

	@BeforeEach
	void setup() {
		User user = save(createUser());
		User otherUser = save(createAnotherUser());
		Category category = save(createIncomeCategory());
		userCategory = save(createUserCategory(user, category));
		otherUserCategory = save(createUserCategory(otherUser, category));
	}

	@Test
	void 유저아이디와_수입아이디로_조회_성공() {
		// given
		Income income = save(createIncome(userCategory));
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
		Income loginIncome = save(createIncome(userCategory));
		Income otherIncome = save(createIncome(otherUserCategory));

		User loginUser = loginIncome.getUser();

		// when
		Optional<Income> findIncome = incomeRepository.findByIdAndUserId(otherIncome.getId(), loginUser.getId());

		// then
		assertThat(findIncome).isEmpty();
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
}
