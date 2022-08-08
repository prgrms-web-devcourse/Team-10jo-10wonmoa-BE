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
		save(createExpenditure(userCategory));
		save(createExpenditure(userCategory));
		save(createExpenditure(userCategory));

		//when
		expenditureRepository.updateUserCategoryAsNull(userCategory.getId());

		//then
		List<UserCategory> categories = expenditureRepository.findAll()
			.stream()
			.map(Expenditure::getUserCategory)
			.collect(Collectors.toList());
		assertThat(categories).containsExactly(null, null, null);
	}

	@Test
	void 해당하는_유저카테고리를_가지는_지출의_카테고리_이름_필드_업데이트() {
		//given
		save(createExpenditure(userCategory));
		save(createExpenditure(userCategory));
		save(createExpenditure(userCategory));

		//when
		// Expenditure의 categoryName 필드 접근 위해 가지고 있는 카테고리를 null 로 변경
		userCategory.updateCategoryAsNull();
		merge(userCategory);

		expenditureRepository.updateCategoryName(userCategory.getId(), "업데이트된카테고리이름");

		//then
		List<Expenditure> expenditures = expenditureRepository.findAll();

		assertThat(expenditures).extracting(Expenditure::getCategoryName)
			.containsExactlyInAnyOrder("업데이트된카테고리이름", "업데이트된카테고리이름", "업데이트된카테고리이름");
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
