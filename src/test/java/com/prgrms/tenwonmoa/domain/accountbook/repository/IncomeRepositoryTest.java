package com.prgrms.tenwonmoa.domain.accountbook.repository;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.assertj.core.api.Assertions.*;

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

	@BeforeEach
	void setup() {
		User user = save(createUser());
		Category category = save(createIncomeCategory());
		userCategory = save(createUserCategory(user, category));
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
	void 수입정보_삭제_성공() {
		Income income = saveIncome();
		Long incomeId = income.getId();

		incomeRepository.deleteById(incomeId);

		Optional<Income> findIncome = incomeRepository.findById(incomeId);
		assertThat(findIncome).isEmpty();
	}
}
