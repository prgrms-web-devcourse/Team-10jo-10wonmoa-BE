package com.prgrms.tenwonmoa.domain.accountbook.repository;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
		category = save(createCategory());
		userCategory = save(new UserCategory(user, category));
	}

	@Nested
	@DisplayName("지출에 대한 일별 쿼리 중")
	class ExpenditureDayQuery {

		@Test
		public void 사용자에대해_지정한_날짜에대한_결과를_리스트로_반환한다() {
			createExpenditures(10);

			List<Expenditure> expenditures = expenditureRepository.findByRegisterDate(user.getId(), LocalDate.now());
			assertThat(expenditures.size()).isEqualTo(10);
		}

	}

	private void createExpenditures(int count) {
		for (int i = 0; i < count; i++) {
			expenditureRepository.save(
				new Expenditure(
					LocalDate.now(),
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
