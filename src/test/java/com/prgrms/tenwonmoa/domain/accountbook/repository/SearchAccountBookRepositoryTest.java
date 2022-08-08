package com.prgrms.tenwonmoa.domain.accountbook.repository;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static com.prgrms.tenwonmoa.domain.accountbook.AccountBookConst.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.prgrms.tenwonmoa.common.RepositoryTest;
import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.dto.AccountBookItem;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;
import com.prgrms.tenwonmoa.domain.user.User;

@DisplayName("가계부 검색 리포지토리 테스트")
class SearchAccountBookRepositoryTest extends RepositoryTest {

	@Autowired
	private SearchAccountBookRepository repository;

	private User user;

	private Category expenditureCategory;
	private Category expenditureCategory2;

	private Category incomeCategory;

	private UserCategory expenditureUserCategory;
	private UserCategory expenditureUserCategory2;

	private UserCategory incomeUserCategory;

	@BeforeEach
	void setup() {
		user = save(createUser());

		expenditureCategory = save(new Category("식비", CategoryType.EXPENDITURE));
		expenditureCategory2 = save(new Category("문화생활", CategoryType.EXPENDITURE));

		incomeCategory = save(new Category("월급", CategoryType.INCOME));

		expenditureUserCategory = save(new UserCategory(user, expenditureCategory));
		expenditureUserCategory2 = save(new UserCategory(user, expenditureCategory2));

		incomeUserCategory = save(new UserCategory(user, incomeCategory));
	}

	@Test
	void 금액_검색_조건으로_조회() {
		//given
		save(new Expenditure(
			LocalDateTime.now().minusDays(2), 1000L, "점심식사",
			expenditureCategory.getName(), user, expenditureUserCategory));

		save(new Expenditure(
			LocalDateTime.now().minusDays(1), 10000L, "영화",
			expenditureCategory2.getName(), user, expenditureUserCategory2));

		save(new Expenditure(
			LocalDateTime.now().minusDays(1), 10000L, "영화관람",
			expenditureCategory2.getName(), user, expenditureUserCategory2));

		save(new Income(
			LocalDateTime.now().minusDays(3), 50000L, "월급1",
			incomeCategory.getName(), user, incomeUserCategory));

		save(new Income(
			LocalDateTime.now(), 100000L, "월급2",
			incomeCategory.getName(), user, incomeUserCategory));

		List<Long> allUserCategoryIds = List.of(expenditureUserCategory.getId(),
			expenditureUserCategory2.getId(), incomeUserCategory.getId());

		//when
		List<AccountBookItem> firstPage = repository.searchAccountBook(
			1000L, 50000L, LEFT_MOST_REGISTER_DATE, RIGHT_MOST_REGISTER_DATE,
			"", allUserCategoryIds, user.getId(), new PageCustomRequest(1, 10));

		//then
		assertThat(firstPage).extracting(AccountBookItem::getAmount)
			.containsExactlyInAnyOrder(1000L, 10000L, 10000L, 50000L);
	}

	@Test
	void 내용_조건으로_조회() {
		//given
		save(new Expenditure(
			LocalDateTime.now().minusDays(2), 1000L, "점심식사",
			expenditureCategory.getName(), user, expenditureUserCategory));

		save(new Expenditure(
			LocalDateTime.now().minusDays(3), 10000L, "영화",
			expenditureCategory2.getName(), user, expenditureUserCategory2));

		save(new Expenditure(
			LocalDateTime.now().minusDays(2), 10000L, "영화관람",
			expenditureCategory2.getName(), user, expenditureUserCategory2));

		save(new Expenditure(
			LocalDateTime.now().minusDays(1), 10000L, "문화 영화 관람",
			expenditureCategory2.getName(), user, expenditureUserCategory2));

		save(new Income(
			LocalDateTime.now().minusDays(1), 10000L, "문화 영화 관람",
			incomeCategory.getName(), user, incomeUserCategory));

		List<Long> allUserCategoryIds = List.of(expenditureUserCategory.getId(), expenditureUserCategory2.getId(),
			incomeUserCategory.getId());

		//when
		List<AccountBookItem> firstPage = repository.searchAccountBook(
			AMOUNT_MIN, AMOUNT_MAX, LEFT_MOST_REGISTER_DATE,
			RIGHT_MOST_REGISTER_DATE, "영화", allUserCategoryIds, user.getId(), new PageCustomRequest(1, 10));

		//then
		assertThat(firstPage).extracting(AccountBookItem::getContent)
			.containsExactly("문화 영화 관람", "문화 영화 관람", "영화관람", "영화");
	}

	@Test
	void 등록날짜_조건으로_조회() {
		//given
		LocalDateTime registerDate = LocalDateTime.now().minusDays(10);
		LocalDateTime registerDate2 = LocalDateTime.now().minusDays(5);
		LocalDateTime registerDate3 = LocalDateTime.now().minusDays(2);
		LocalDateTime registerDate4 = LocalDateTime.now().minusDays(1);

		save(new Expenditure(
			registerDate, 1000L, "점심식사",
			expenditureCategory.getName(), user, expenditureUserCategory));

		save(new Expenditure(
			registerDate2, 10000L, "영화",
			expenditureCategory2.getName(), user, expenditureUserCategory2));

		save(new Expenditure(
			registerDate3, 10000L, "영화관람",
			expenditureCategory2.getName(), user, expenditureUserCategory2));

		save(new Expenditure(
			registerDate4, 10000L, "문화 영화 관람",
			expenditureCategory2.getName(), user, expenditureUserCategory2));

		List<Long> allUserCategoryIds = List.of(expenditureUserCategory.getId(), expenditureUserCategory2.getId());

		//when
		List<AccountBookItem> results = repository.searchAccountBook(
			AMOUNT_MIN, AMOUNT_MAX, LocalDate.now().minusDays(6),
			LocalDate.now(), "", allUserCategoryIds, user.getId(), new PageCustomRequest(1, 10));

		//then
		assertThat(results).extracting(AccountBookItem::getRegisterTime)
			.containsExactly(registerDate4, registerDate3, registerDate2);
	}

	@Test
	void 카테고리_아이디로_조회() {
		//given
		save(new Expenditure(
			LocalDateTime.now().minusDays(10), 1000L, "점심식사",
			expenditureCategory.getName(), user, expenditureUserCategory));

		save(new Expenditure(
			LocalDateTime.now().minusDays(5), 10000L, "영화",
			expenditureCategory2.getName(), user, expenditureUserCategory2));

		save(new Expenditure(
			LocalDateTime.now().minusDays(2), 10000L, "영화관람",
			expenditureCategory2.getName(), user, expenditureUserCategory2));

		save(new Expenditure(
			LocalDateTime.now().minusDays(1), 10000L, "문화 영화 관람",
			expenditureCategory2.getName(), user, expenditureUserCategory2));
		//when
		List<AccountBookItem> results = repository.searchAccountBook(
			AMOUNT_MIN, AMOUNT_MAX, LEFT_MOST_REGISTER_DATE,
			RIGHT_MOST_REGISTER_DATE, "", List.of(expenditureUserCategory.getId()), user.getId(),
			new PageCustomRequest(1, 10));

		List<AccountBookItem> results2 = repository.searchAccountBook(
			AMOUNT_MIN, AMOUNT_MAX, LEFT_MOST_REGISTER_DATE,
			RIGHT_MOST_REGISTER_DATE, "", List.of(expenditureUserCategory2.getId()), user.getId(),
			new PageCustomRequest(1, 10));

		//then
		assertThat(results).hasSize(1);
		assertThat(results2).hasSize(3);
	}
}
