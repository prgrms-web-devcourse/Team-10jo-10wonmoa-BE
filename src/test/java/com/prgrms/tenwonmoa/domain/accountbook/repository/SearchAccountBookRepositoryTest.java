package com.prgrms.tenwonmoa.domain.accountbook.repository;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static com.prgrms.tenwonmoa.domain.accountbook.AccountBookConst.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.prgrms.tenwonmoa.common.fixture.RepositoryFixture;
import com.prgrms.tenwonmoa.domain.accountbook.dto.AccountBookItem;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;
import com.prgrms.tenwonmoa.domain.user.User;

@DisplayName("가계부 검색 리포지토리 테스트")
class SearchAccountBookRepositoryTest extends RepositoryFixture {

	private List<Long> allUserCategoryIds;

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

		allUserCategoryIds = new ArrayList<>(List.of(expenditureUserCategory.getId(), expenditureUserCategory2.getId(),
			incomeUserCategory.getId()));
	}

	@Test
	void 금액_검색_조건으로_조회() {
		//given
		saveExpenditure(defaultTime, 1000L, defaultContent, expenditureCategory.getName(), user,
			expenditureUserCategory);
		saveExpenditure(defaultTime, 10000L, defaultContent, expenditureCategory.getName(), user,
			expenditureUserCategory);
		saveExpenditure(defaultTime, 20000L, defaultContent, expenditureCategory.getName(), user,
			expenditureUserCategory);
		saveExpenditure(defaultTime, 50000L, defaultContent, expenditureCategory.getName(), user,
			expenditureUserCategory);
		saveIncome(defaultTime, 100000L, defaultContent, incomeCategory.getName(), user, incomeUserCategory);

		//when
		List<AccountBookItem> items = repository.searchAccountBook(
			1000L, 50000L, LEFT_MOST_REGISTER_DATE.atTime(LocalTime.MIN),
			RIGHT_MOST_REGISTER_DATE.atTime(LocalTime.MAX),
			defaultContent, allUserCategoryIds, user.getId(), new PageCustomRequest(1, 10));

		List<AccountBookItem> items2 = repository.searchAccountBook(
			50000L, 100000L, LEFT_MOST_REGISTER_DATE.atTime(LocalTime.MIN),
			RIGHT_MOST_REGISTER_DATE.atTime(LocalTime.MAX),
			defaultContent, allUserCategoryIds, user.getId(), new PageCustomRequest(1, 10));

		//then
		assertThat(items).extracting(AccountBookItem::getAmount)
			.containsExactlyInAnyOrder(1000L, 10000L, 20000L, 50000L);

		assertThat(items2).extracting(AccountBookItem::getAmount)
			.containsExactlyInAnyOrder(100000L, 50000L);
	}

	@Test
	void 내용_조건으로_조회() {
		//given
		saveExpenditure(defaultTime, defaultAmount, "점심식사",
			expenditureCategory.getName(), user, expenditureUserCategory);

		saveExpenditure(defaultTime, defaultAmount, "영화",
			expenditureCategory.getName(), user, expenditureUserCategory);

		saveExpenditure(defaultTime, defaultAmount, "영화관람",
			expenditureCategory.getName(), user, expenditureUserCategory);

		saveExpenditure(defaultTime, defaultAmount, "문화 영화 관람",
			expenditureCategory.getName(), user, expenditureUserCategory);

		saveIncome(defaultTime, defaultAmount, "영화 수입",
			incomeCategory.getName(), user, incomeUserCategory);

		//when
		List<AccountBookItem> items = repository.searchAccountBook(
			AMOUNT_MIN, AMOUNT_MAX, LEFT_MOST_REGISTER_DATE.atTime(LocalTime.MIN),
			RIGHT_MOST_REGISTER_DATE.atTime(LocalTime.MAX), "영화", allUserCategoryIds, user.getId(),
			new PageCustomRequest(1, 10));

		//then
		assertThat(items).extracting(AccountBookItem::getContent)
			.containsExactlyInAnyOrder("문화 영화 관람", "영화 수입", "영화관람", "영화");
	}

	@Test
	void 등록날짜_조건으로_조회() {
		//given
		LocalDateTime latestDate = LocalDateTime.now().minusDays(1);
		LocalDateTime secondDate = LocalDateTime.now().minusDays(2);
		LocalDateTime thirdDate = LocalDateTime.now().minusDays(5);
		LocalDateTime fourthDate = LocalDateTime.now().minusDays(10);

		saveExpenditure(latestDate, defaultAmount, defaultContent, expenditureCategory.getName(), user,
			expenditureUserCategory);
		saveExpenditure(secondDate, defaultAmount, defaultContent, expenditureCategory.getName(), user,
			expenditureUserCategory);
		saveExpenditure(thirdDate, defaultAmount, defaultContent, expenditureCategory.getName(), user,
			expenditureUserCategory);
		saveExpenditure(fourthDate, defaultAmount, defaultContent, expenditureCategory.getName(), user,
			expenditureUserCategory);

		//when
		List<AccountBookItem> items = repository.searchAccountBook(
			AMOUNT_MIN, AMOUNT_MAX, LocalDate.now().minusDays(6).atTime(LocalTime.MIN),
			LocalDate.now().atTime(LocalTime.MAX), defaultContent, allUserCategoryIds, user.getId(),
			new PageCustomRequest(1, 10));

		List<AccountBookItem> items2 = repository.searchAccountBook(
			AMOUNT_MIN, AMOUNT_MAX, LocalDate.now().minusDays(10).atTime(LocalTime.MIN),
			LocalDate.now().minusDays(4).atTime(LocalTime.MAX), defaultContent,
			allUserCategoryIds, user.getId(), new PageCustomRequest(1, 10));

		//then
		assertThat(items).extracting(AccountBookItem::getRegisterTime)
			.containsExactly(latestDate, secondDate, thirdDate);

		assertThat(items2).extracting(AccountBookItem::getRegisterTime)
			.containsExactly(thirdDate, fourthDate);
	}

	@Test
	void 카테고리_아이디로_조회() {
		//given

		// expenditureUserCategory
		saveExpenditure(defaultTime, defaultAmount, defaultContent, expenditureUserCategory.getCategoryName(), user,
			expenditureUserCategory);

		// expenditureUserCategory2
		saveExpenditure(defaultTime, defaultAmount, defaultContent, expenditureUserCategory2.getCategoryName(), user,
			expenditureUserCategory2);

		saveExpenditure(defaultTime, defaultAmount, defaultContent, expenditureUserCategory2.getCategoryName(), user,
			expenditureUserCategory2);

		// incomeUserCategory
		saveIncome(defaultTime, defaultAmount, defaultContent, incomeUserCategory.getCategoryName(), user,
			incomeUserCategory);

		saveIncome(defaultTime, defaultAmount, defaultContent, incomeUserCategory.getCategoryName(), user,
			incomeUserCategory);

		//when
		List<AccountBookItem> expenditureUserCategoryItems = repository.searchAccountBook(
			AMOUNT_MIN, AMOUNT_MAX, LEFT_MOST_REGISTER_DATE.atTime(LocalTime.MIN),
			RIGHT_MOST_REGISTER_DATE.atTime(LocalTime.MAX), defaultContent, List.of(expenditureUserCategory.getId()),
			user.getId(),
			new PageCustomRequest(1, 10));

		List<AccountBookItem> expenditureUserCategory2Items = repository.searchAccountBook(
			AMOUNT_MIN, AMOUNT_MAX, LEFT_MOST_REGISTER_DATE.atTime(LocalTime.MIN),
			RIGHT_MOST_REGISTER_DATE.atTime(LocalTime.MAX), defaultContent, List.of(expenditureUserCategory2.getId()),
			user.getId(),
			new PageCustomRequest(1, 10));

		List<AccountBookItem> incomeUserCategoryItems = repository.searchAccountBook(
			AMOUNT_MIN, AMOUNT_MAX, LEFT_MOST_REGISTER_DATE.atTime(LocalTime.MIN),
			RIGHT_MOST_REGISTER_DATE.atTime(LocalTime.MAX), defaultContent, List.of(incomeUserCategory.getId()),
			user.getId(),
			new PageCustomRequest(1, 10));

		//then
		assertThat(expenditureUserCategoryItems).hasSize(1);
		assertThat(expenditureUserCategory2Items).hasSize(2);
		assertThat(incomeUserCategoryItems).hasSize(2);
	}

	@Test
	void 유저아이디로_조회() {
		//given
		User otherUser = save(createAnotherUser());
		UserCategory otherUserExpenditureCategory = save(createUserCategory(otherUser, expenditureCategory));
		UserCategory otherUserIncomeCategory = save(createUserCategory(otherUser, incomeCategory));

		allUserCategoryIds.add(otherUserExpenditureCategory.getId());
		allUserCategoryIds.add(otherUserIncomeCategory.getId());

		// user
		saveExpenditure(defaultTime, defaultAmount, defaultContent,
			expenditureUserCategory.getCategoryName(), user, expenditureUserCategory);

		saveIncome(defaultTime, defaultAmount, defaultContent,
			incomeUserCategory.getCategoryName(), user, incomeUserCategory);

		saveIncome(defaultTime, defaultAmount, defaultContent,
			incomeUserCategory.getCategoryName(), user, incomeUserCategory);

		// other user
		saveExpenditure(defaultTime, defaultAmount, defaultContent,
			otherUserExpenditureCategory.getCategoryName(), otherUser, otherUserExpenditureCategory);

		saveIncome(defaultTime, defaultAmount, defaultContent,
			otherUserIncomeCategory.getCategoryName(), otherUser, otherUserIncomeCategory);

		//when
		List<AccountBookItem> userItems = repository.searchAccountBook(
			AMOUNT_MIN, AMOUNT_MAX, LEFT_MOST_REGISTER_DATE.atTime(LocalTime.MIN),
			RIGHT_MOST_REGISTER_DATE.atTime(LocalTime.MAX), defaultContent, allUserCategoryIds, user.getId(),
			new PageCustomRequest(1, 10));

		List<AccountBookItem> otherUserItems = repository.searchAccountBook(
			AMOUNT_MIN, AMOUNT_MAX, LEFT_MOST_REGISTER_DATE.atTime(LocalTime.MIN),
			RIGHT_MOST_REGISTER_DATE.atTime(LocalTime.MAX), defaultContent, allUserCategoryIds, otherUser.getId(),
			new PageCustomRequest(1, 10));

		//then
		assertThat(userItems).extracting(AccountBookItem::getType)
			.containsExactlyInAnyOrder(
				CategoryType.EXPENDITURE.name(), CategoryType.INCOME.name(), CategoryType.INCOME.name());

		assertThat(otherUserItems).extracting(AccountBookItem::getType)
			.containsExactlyInAnyOrder(
				CategoryType.EXPENDITURE.name(), CategoryType.INCOME.name());

	}

	@Test
	void 내용이_공백인_데이터_조회() {
		//given
		saveExpenditure(defaultTime, defaultAmount, "",
			expenditureUserCategory.getCategoryName(), user, expenditureUserCategory);

		saveIncome(defaultTime, defaultAmount, "",
			incomeUserCategory.getCategoryName(), user, incomeUserCategory);

		//when
		List<AccountBookItem> items = repository.searchAccountBook(
			AMOUNT_MIN, AMOUNT_MAX, LEFT_MOST_REGISTER_DATE.atTime(LocalTime.MIN),
			RIGHT_MOST_REGISTER_DATE.atTime(LocalTime.MAX), "", allUserCategoryIds, user.getId(),
			new PageCustomRequest(1, 10));

		//then
		assertThat(items).hasSize(2);
	}
}
