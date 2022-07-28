package com.prgrms.tenwonmoa.domain.accountbook.repository;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.prgrms.tenwonmoa.common.RepositoryTest;
import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;

@DisplayName("수입 리포지토리 테스트")
class IncomeRepositoryTest extends RepositoryTest {

	@Autowired
	private IncomeRepository incomeRepository;

	private User user;

	private Category category;

	private UserCategory userCategory;

	@BeforeEach
	void setup() {
		user = save(createUser());
		category = save(createCategory());
		userCategory = save(new UserCategory(user, category));
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
