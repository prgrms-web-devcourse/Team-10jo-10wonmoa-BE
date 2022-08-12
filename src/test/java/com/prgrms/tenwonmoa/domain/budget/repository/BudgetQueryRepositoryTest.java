package com.prgrms.tenwonmoa.domain.budget.repository;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static com.prgrms.tenwonmoa.domain.category.CategoryType.*;
import static java.time.LocalDateTime.*;
import static org.assertj.core.api.Assertions.*;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.prgrms.tenwonmoa.common.fixture.RepositoryFixture;
import com.prgrms.tenwonmoa.domain.budget.dto.FindBudgetByRegisterDate;
import com.prgrms.tenwonmoa.domain.budget.dto.FindBudgetData;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;

class BudgetQueryRepositoryTest extends RepositoryFixture {
	private User user;
	private User otherUser;
	private YearMonth now = YearMonth.of(2022, 1);

	// user -> category1
	private UserCategory uc1;
	// user -> category2
	private UserCategory uc2;
	// user -> category3
	private UserCategory uc3;
	// otherUser -> category4
	private UserCategory uc4;
	// otherUser -> category5
	private UserCategory uc5;

	@Autowired
	private BudgetQueryRepository budgetQueryRepository;

	@BeforeEach
	void init() {
		user = saveRandomUser();
		otherUser = saveRandomUser();
		initBudgetData();
	}

	@Test
	void 유저의카테고리별_예산금액_조회_성공() {
		// given
		saveBudget(200L, now, user, uc1);
		saveBudget(100L, now, user, uc2);
		// when
		List<FindBudgetData> findBudgetData = budgetQueryRepository.searchUserCategoriesWithBudget(user.getId(), now);

		assertThat(findBudgetData).hasSize(3);
		assertThat(findBudgetData).extracting("amount").containsExactly(200L, 100L, 0L);
	}

	@Test
	void 등록된_예산이_없는경우() {
		// when
		List<FindBudgetData> findBudgetData = budgetQueryRepository.searchUserCategoriesWithBudget(otherUser.getId(),
			now);

		assertThat(findBudgetData).hasSize(2);
		assertThat(findBudgetData).extracting("amount").containsExactly(0L, 0L);
	}

	@Test
	void 데이터가없는_날짜로_요청하는_경우() {
		// given
		saveBudget(200L, now, user, uc1);
		saveBudget(100L, now, user, uc2);
		// when
		List<FindBudgetData> findBudgetData = budgetQueryRepository.searchUserCategoriesWithBudget(
			otherUser.getId(),
			now.plusMonths(1));

		assertThat(findBudgetData).hasSize(2);
		assertThat(findBudgetData).extracting("amount").containsExactly(0L, 0L);
	}

	@Test
	void 여러_유저의_예산이등록된_경우_잘찾아오는지_테스트() {
		// given
		saveBudget(200L, now, user, uc3);

		saveBudget(10L, now, otherUser, uc4);
		saveBudget(20L, now, otherUser, uc5);
		// when
		List<FindBudgetData> budgets1 = budgetQueryRepository.searchUserCategoriesWithBudget(user.getId(), now);
		List<FindBudgetData> budgets2 = budgetQueryRepository.searchUserCategoriesWithBudget(otherUser.getId(), now);

		assertThat(budgets1).hasSize(3);
		assertThat(budgets1).extracting("amount").containsExactly(200L, 0L, 0L);
		assertThat(budgets2).hasSize(2);
		assertThat(budgets2).extracting("amount").containsExactly(20L, 10L);
	}

	@Test
	void 월별_예산조회_성공() {
		// given
		saveBudget(100L, now, user, uc1);
		saveBudget(200L, now, user, uc2);
		saveBudget(300L, now, otherUser, uc4);
		// when
		List<FindBudgetByRegisterDate> result = budgetQueryRepository.searchBudgetByRegisterDate(
			user.getId(), now.getYear(),
			now.getMonthValue());
		// then
		assertThat(result).hasSize(2);
		assertThat(result).extracting((data) -> data.getCategoryName(), (data) -> data.getAmount())
			.contains(tuple("ct1", 100L), tuple("ct2", 200L));
	}

	@Test
	void 월별_예산조회_0원_예산은_제외() {
		// given
		saveBudget(100L, now, user, uc3);
		saveBudget(0L, now, user, uc2);
		saveBudget(0L, now, user, uc1);

		// when
		List<FindBudgetByRegisterDate> result = budgetQueryRepository.searchBudgetByRegisterDate(
			user.getId(), now.getYear(),
			now.getMonthValue());
		// then
		assertThat(result).hasSize(1);
		assertThat(result).extracting((data) -> data.getCategoryName(), (data) -> data.getAmount())
			.contains(tuple("ct3", 100L));
	}

	@Test
	void 월별_예산조회_같은_이름의카테고리_따로조회() {
		// given
		Category sameNameCategory = save(new Category("ct1", EXPENDITURE));
		UserCategory sameCategory = save(createUserCategory(user, sameNameCategory));

		// 같은 이름의 카테고리로 예산을 저장
		saveBudget(100L, now, user, uc1);
		saveBudget(200L, now, user, sameCategory);

		// when
		List<FindBudgetByRegisterDate> result = budgetQueryRepository.searchBudgetByRegisterDate(
			user.getId(), now.getYear(),
			now.getMonthValue());
		// then
		assertThat(result).hasSize(2);
		assertThat(result).extracting((data) -> data.getCategoryName(), (data) -> data.getAmount())
			.contains(tuple("ct1", 100L), tuple("ct1", 200L));
	}

	@Test
	void 연별_예산이존재하는_지출조회_같은이름의_카테고리() {
		// given
		Category sameNameCategory = save(new Category("ct1", EXPENDITURE));
		Category sameNameCategory2 = save(new Category("ct1", EXPENDITURE));
		UserCategory sameCategory = save(createUserCategory(user, sameNameCategory));
		UserCategory sameCategory2 = save(createUserCategory(user, sameNameCategory2));

		// 같은 이름의 카테고리로 예산을 저장
		saveBudget(100L, now, user, uc1);
		saveBudget(200L, now.plusMonths(1), user, sameCategory);
		saveBudget(300L, now.plusMonths(2), user, sameCategory2);

		// when
		List<FindBudgetByRegisterDate> result = budgetQueryRepository.searchBudgetByRegisterDate(
			user.getId(), now.getYear(), null);

		assertThat(result).hasSize(3);
	}

	@Test
	void 연별_예산조회_성공() {
		// given
		saveBudget(1L, YearMonth.of(now.getYear(), 1), user, uc3);
		saveBudget(1L, YearMonth.of(now.getYear(), 2), user, uc3);
		saveBudget(1L, YearMonth.of(now.getYear(), 3), user, uc3);
		saveBudget(1L, YearMonth.of(now.getYear(), 4), user, uc3);
		saveBudget(1L, YearMonth.of(now.getYear(), 5), user, uc1);
		saveBudget(1L, YearMonth.of(now.getYear(), 6), user, uc1);
		saveBudget(1L, YearMonth.of(now.getYear() - 1, 3), user, uc1);
		saveBudget(1L, YearMonth.of(now.getYear() - 1, 7), user, uc1);
		// when
		List<FindBudgetByRegisterDate> result = budgetQueryRepository.searchBudgetByRegisterDate(
			user.getId(), now.getYear(), null);
		List<FindBudgetByRegisterDate> result2 = budgetQueryRepository.searchBudgetByRegisterDate(
			user.getId(), now.getYear() - 1, null);
		// then
		assertThat(result).hasSize(2);
		assertThat(result2).hasSize(1);
		assertThat(result).extracting((data) -> data.getCategoryName(), (data) -> data.getAmount())
			.contains(tuple("ct3", 4L), tuple("ct1", 2L));
		assertThat(result2).extracting((data) -> data.getCategoryName(), (data) -> data.getAmount())
			.contains(tuple("ct1", 2L));
	}

	@Test
	void 연별_예산조회_0원_예산은_제외() {
		// given
		saveBudget(1L, YearMonth.of(now.getYear(), 1), user, uc1);
		saveBudget(0L, YearMonth.of(now.getYear(), 2), user, uc1);
		saveBudget(2L, YearMonth.of(now.getYear(), 3), user, uc1);
		saveBudget(0L, YearMonth.of(now.getYear(), 12), user, uc1);

		// when
		List<FindBudgetByRegisterDate> result = budgetQueryRepository.searchBudgetByRegisterDate(
			user.getId(), now.getYear(),
			null);
		// then
		assertThat(result).hasSize(1);
		assertThat(result).extracting((data) -> data.getCategoryName(), (data) -> data.getAmount())
			.contains(tuple("ct1", 3L));
	}

	@Test
	void 월별_예산이존재하는_지출조회() {
		// given
		// 예산은 uc1에만 등록되어있다.
		saveBudget(100L, now, user, uc1);
		// 지출은 uc1, uc2, uc3 모두 발생했다.
		saveExpenditure(uc1, 1L, of(now.getYear(), now.getMonthValue(), 1, 0, 0));
		saveExpenditure(uc2, 10L, of(now.getYear(), now.getMonthValue(), 2, 0, 0));
		saveExpenditure(uc3, 100L, of(now.getYear(), now.getMonthValue(), 3, 0, 0));

		// when
		Map<Long, Long> result = budgetQueryRepository.searchExpendituresExistBudget(
			user.getId(), now.getYear(), now.getMonthValue());

		assertThat(result).hasSize(1);
		assertThat(result).containsEntry(uc1.getId(), 1L);
	}

	@Test
	void 월별_예산이존재하는_지출_동일한_이름카테고리_조회() {
		// given
		Category sameNameCategory = save(new Category("ct1", EXPENDITURE));
		Category sameNameCategory2 = save(new Category("ct1", EXPENDITURE));
		UserCategory sameCategory = save(createUserCategory(user, sameNameCategory));
		UserCategory sameCategory2 = save(createUserCategory(user, sameNameCategory2));
		// 예산은 uc1에만 등록되어있다.
		saveBudget(100L, now, user, uc1);
		saveBudget(100L, now, user, sameCategory);
		saveBudget(100L, now, user, sameCategory2);
		// 지출은 uc1, uc2, uc3 모두 발생했다.
		saveExpenditure(uc1, 1L, of(now.getYear(), now.getMonthValue(), 1, 0, 0));
		saveExpenditure(sameCategory, 10L, of(now.getYear(), now.getMonthValue(), 2, 0, 0));
		saveExpenditure(sameCategory2, 100L, of(now.getYear(), now.getMonthValue(), 3, 0, 0));

		// when
		Map<Long, Long> result = budgetQueryRepository.searchExpendituresExistBudget(
			user.getId(), now.getYear(), now.getMonthValue());

		assertThat(result).hasSize(3);
		assertThat(result).containsEntry(uc1.getId(), 1L);
		assertThat(result).containsEntry(sameCategory.getId(), 10L);
		assertThat(result).containsEntry(sameCategory2.getId(), 100L);
	}

	@Test
	void 월별_예산이존재하는_지출조회_지출데이터가_없을때() {
		// given
		// 예산은 uc1에만 등록되어있다. 아무 지출이 발생하지 않음
		saveBudget(100L, now, user, uc1);

		// when
		List<FindBudgetByRegisterDate> result = budgetQueryRepository.searchBudgetByRegisterDate(
			user.getId(), now.getYear(), now.getMonthValue());

		assertThat(result).hasSize(1);
	}

	@Test
	void 연별_예산이존재하는_지출조회() {
		// given
		// uc1은 1, 2, 3월에 100원씩 예산등록
		saveBudget(100L, now, user, uc1);
		saveBudget(100L, now.plusMonths(1), user, uc1);
		saveBudget(100L, now.plusMonths(2), user, uc1);

		// uc2는 2월에 200원
		saveBudget(200L, now.plusMonths(1), user, uc2);

		// uc3은 5월에 300원
		saveBudget(300L, now.plusMonths(4), user, uc3);

		// uc1은 3, 4, 5 월에 지출이 발생 (4, 5월은 예산이 등록안되었지만 연별 조회에는 포함 됨)
		saveExpenditure(uc1, 120L, of(now.getYear(), 4, 1, 0, 0));
		saveExpenditure(uc1, 10L, of(now.getYear(), 5, 2, 0, 0));

		// uc2는 2월에 80원 지출
		saveExpenditure(uc2, 80L, of(now.getYear(), 2, 2, 0, 0));

		// when
		Map<Long, Long> result = budgetQueryRepository.searchExpendituresExistBudget(
			user.getId(), now.getYear(), null);

		assertThat(result).hasSize(2);
		assertThat(result).containsEntry(uc1.getId(), 130L);
		assertThat(result).containsEntry(uc2.getId(), 80L);
	}

	@Test
	private void initBudgetData() {
		Category category1 = save(new Category("ct1", EXPENDITURE));
		Category category2 = save(new Category("ct2", EXPENDITURE));
		Category category3 = save(new Category("ct3", EXPENDITURE));
		Category category4 = save(new Category("ct4", EXPENDITURE));
		Category category5 = save(new Category("ct5", EXPENDITURE));

		uc1 = save(createUserCategory(user, category1));
		uc2 = save(createUserCategory(user, category2));
		uc3 = save(createUserCategory(user, category3));
		uc4 = save(createUserCategory(otherUser, category4));
		uc5 = save(createUserCategory(otherUser, category5));
	}
}
