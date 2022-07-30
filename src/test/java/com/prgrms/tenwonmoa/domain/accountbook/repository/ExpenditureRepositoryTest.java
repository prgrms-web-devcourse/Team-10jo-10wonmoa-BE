package com.prgrms.tenwonmoa.domain.accountbook.repository;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.prgrms.tenwonmoa.common.RepositoryTest;
import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;

@DisplayName("Expenditure(지출) 레포지토리 테스트")
class ExpenditureRepositoryTest extends RepositoryTest {

	@Autowired
	private ExpenditureRepository expenditureRepository;

	private User user;

	private Category category;

	private UserCategory userCategory;

	@BeforeEach
	void setup() {
		user = save(createUser());
		category = save(createIncomeCategory());
		userCategory = save(new UserCategory(user, category));
	}

	@Test
	void 해당하는_유저카테고리_아이디를_가진_지출의_유저카테고리를_null_로_업데이트() {
		//given
		Expenditure expenditure = new Expenditure(
			LocalDateTime.now(), 10000L, "내용", category.getName(), user, userCategory);

		Expenditure expenditure2 = new Expenditure(
			LocalDateTime.now(), 10000L, "내용", category.getName(), user, userCategory);

		Expenditure expenditure3 = new Expenditure(
			LocalDateTime.now(), 10000L, "내용", category.getName(), user, userCategory);

		expenditureRepository.saveAll(List.of(expenditure, expenditure2, expenditure3));

		//when
		expenditureRepository.updateUserCategoryAsNull(userCategory.getId());

		//then
		List<UserCategory> categories = expenditureRepository.findAll()
			.stream()
			.map(Expenditure::getUserCategory)
			.collect(Collectors.toList());
		assertThat(categories).containsExactly(null, null, null);
	}

	private void createExpenditures(int count) {
		for (int i = 0; i < count; i++) {
			expenditureRepository.save(
				new Expenditure(
					LocalDateTime.now(),
					10000L + i,
					"내용" + i,
					category.getName(),
					user,
					userCategory
				)
			);
		}
	}
}
