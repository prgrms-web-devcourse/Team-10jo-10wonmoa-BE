package com.prgrms.tenwonmoa.domain.accountbook.repository;

import static com.prgrms.tenwonmoa.domain.category.CategoryType.*;
import static java.time.LocalDateTime.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.prgrms.tenwonmoa.common.fixture.RepositoryFixture;
import com.prgrms.tenwonmoa.domain.accountbook.dto.statistics.FindStatisticsData;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;

class StatisticsQueryRepositoryTest extends RepositoryFixture {
	private static final String NAME = "name";
	private static final String TOTAL = "total";
	private static final List<String> INCOME_DEFAULT = List.of("용돈", "상여", "금융소득");
	private static final List<String> EXPENDITURE_DEFAULT = List.of("교통/차량", "문화생활", "마트/편의점");
	private static final Long AMOUNT = 10L;
	@Autowired
	EntityManager em;
	@Autowired
	StatisticsQueryRepository statisticsQueryRepository;
	User user;
	UserCategory incomeUserCategory2;
	Category incomeCategory3;
	UserCategory expenditureUserCategory2;
	Category expenditureCategory3;

	@BeforeEach
	void init() {
		user = saveRandomUser();
		initIncomeData();
		initExpenditureData();
	}

	@Test
	void 수입_월조건통계조회_성공() {
		// when
		List<FindStatisticsData> incomes = statisticsQueryRepository
			.searchIncomeByRegisterDate(user.getId(), 2022, 07);
		// then
		assertThat(incomes).hasSize(3);
		assertThat(incomes).extracting(NAME)
			.containsExactly(INCOME_DEFAULT.get(0), INCOME_DEFAULT.get(1), INCOME_DEFAULT.get(2));
		assertThat(incomes).extracting(TOTAL)
			.containsExactly(40L, 30L, 20L);
	}

	@Test
	void 수입_월조건통계조회_년도없으면_예외() {
		assertThrows(IllegalArgumentException.class, () ->
			statisticsQueryRepository.searchIncomeByRegisterDate(user.getId(), null, 07));
	}

	@Test
	void 수입_년조건_통계_월_null_일때_성공() {
		// when
		List<FindStatisticsData> findStatisticsData = statisticsQueryRepository.searchIncomeByRegisterDate(user.getId(),
			2022,
			null);
		// then
		assertThat(findStatisticsData).hasSize(3);
	}

	@Test
	void 유저카테고리_삭제시_카테고리의이름을_참조한다() {
		// given
		UserCategory findUserCategory2 = em.find(UserCategory.class, incomeUserCategory2.getId());
		findUserCategory2.updateCategoryAsNull();
		save(findUserCategory2);
		// when
		List<FindStatisticsData> incomes = statisticsQueryRepository
			.searchIncomeByRegisterDate(user.getId(), 2022, 07);
		// then
		assertThat(incomes).hasSize(3);
		assertThat(incomes).extracting(NAME)
			.containsExactly(INCOME_DEFAULT.get(0), INCOME_DEFAULT.get(1), INCOME_DEFAULT.get(2));
		assertThat(incomes).extracting(NAME)
			.doesNotContainNull();
	}
	@Test
	void 카테고리이름_변경시_변경된이름을_참조한다() {
		// given
		Category findCategory3 = em.find(Category.class, incomeCategory3.getId());
		String updateCategoryName = "KAN-TE";
		findCategory3.updateName(updateCategoryName);
		save(findCategory3);
		// when
		List<FindStatisticsData> incomes = statisticsQueryRepository
			.searchIncomeByRegisterDate(user.getId(), 2022, 07);
		// then
		assertThat(incomes).hasSize(3);
		assertThat(incomes).extracting(NAME)
			.containsExactly(INCOME_DEFAULT.get(0), INCOME_DEFAULT.get(1), updateCategoryName);
		assertThat(incomes).extracting(NAME)
			.doesNotContain(INCOME_DEFAULT.get(2));
	}
	@Test
	void 지출_월조건통계조회_성공() {
		// when
		List<FindStatisticsData> findData = statisticsQueryRepository
			.searchExpenditureByRegisterDate(user.getId(), 2022, 07);
		// then
		assertThat(findData).hasSize(3);
		assertThat(findData).extracting(NAME)
			.containsExactly(EXPENDITURE_DEFAULT.get(2), EXPENDITURE_DEFAULT.get(1), EXPENDITURE_DEFAULT.get(0));
		assertThat(findData).extracting(TOTAL)
			.containsExactly(30L, 20L, 10L);
	}

	@Test
	void 지출_월조건통계조회_년도없으면_예외() {
		assertThrows(IllegalArgumentException.class, () ->
			statisticsQueryRepository.searchExpenditureByRegisterDate(user.getId(), null, 07));
	}

	@Test
	void 지출_년조건_통계_월_null_일때_성공() {
		List<FindStatisticsData> findStatisticsData = statisticsQueryRepository.searchExpenditureByRegisterDate(
			user.getId(),
			2022,
			null);
		assertThat(findStatisticsData).hasSize(3);
	}

	@Test
	void 지출_유저카테고리_삭제시_카테고리의이름을_참조한다() {
		// given
		UserCategory findUserCategory2 = em.find(UserCategory.class, expenditureUserCategory2.getId());
		findUserCategory2.updateCategoryAsNull();
		save(findUserCategory2);
		// when
		List<FindStatisticsData> findData = statisticsQueryRepository
			.searchExpenditureByRegisterDate(user.getId(), 2022, 07);
		// then
		assertThat(findData).hasSize(3);
		assertThat(findData).extracting(NAME)
			.containsExactly(EXPENDITURE_DEFAULT.get(2), EXPENDITURE_DEFAULT.get(1), EXPENDITURE_DEFAULT.get(0));
		assertThat(findData).extracting(NAME)
			.doesNotContainNull();
	}

	@Test
	void 지출_카테고리이름_변경시_변경된이름을_참조한다() {
		// given
		Category findCategory3 = em.find(Category.class, expenditureCategory3.getId());
		String updateCategoryName = "KAN-TE";
		findCategory3.updateName(updateCategoryName);
		save(findCategory3);
		// when
		List<FindStatisticsData> findData = statisticsQueryRepository
			.searchExpenditureByRegisterDate(user.getId(), 2022, 07);
		// then
		assertThat(findData).hasSize(3);
		assertThat(findData).extracting(NAME)
			.containsExactly(updateCategoryName, EXPENDITURE_DEFAULT.get(1), EXPENDITURE_DEFAULT.get(0));
		assertThat(findData).extracting(NAME)
			.doesNotContain(EXPENDITURE_DEFAULT.get(2));
	}

	private void initIncomeData() {
		Category incomeCategory1 = save(new Category(INCOME_DEFAULT.get(0), INCOME));
		Category incomeCategory2 = save(new Category(INCOME_DEFAULT.get(1), INCOME));
		incomeCategory3 = save(new Category(INCOME_DEFAULT.get(2), INCOME));

		UserCategory incomeUserCategory1 = saveUserCategory(user, incomeCategory1);
		incomeUserCategory2 = saveUserCategory(user, incomeCategory2);
		UserCategory incomeUserCategory3 = saveUserCategory(user, incomeCategory3);

		// 2022.07월달에 category1으로 4건의 수입등록
		iterateFixture(4, (i) -> saveIncome(incomeUserCategory1, AMOUNT, of(2022, 7, i, 0, 0, 0)));
		// 2022.07월달에 category2으로 3건의 수입등록
		iterateFixture(3, (i) -> saveIncome(incomeUserCategory2, AMOUNT, of(2022, 7, i, 0, 0, 0)));
		// 2022.07월달에 category3으로 2건의 수입등록
		iterateFixture(2, (i) -> saveIncome(incomeUserCategory3, AMOUNT, of(2022, 7, i, 0, 0, 0)));
		// 2022.06월달에 category1으로 2건의 수입등록
		iterateFixture(2, (i) -> saveIncome(incomeUserCategory1, AMOUNT, of(2022, 6, i, 0, 0, 0)));
		// 2021.07월달에 category1으로 2건의 수입등록
		iterateFixture(2, (i) -> saveIncome(incomeUserCategory1, AMOUNT, of(2022, 2, i, 0, 0, 0)));
	}

	private void initExpenditureData() {
		Category expenditureCategory1 = save(new Category(EXPENDITURE_DEFAULT.get(0), EXPENDITURE));
		Category expenditureCategory2 = save(new Category(EXPENDITURE_DEFAULT.get(1), EXPENDITURE));
		expenditureCategory3 = save(new Category(EXPENDITURE_DEFAULT.get(2), EXPENDITURE));

		UserCategory expenditureUserCategory1 = saveUserCategory(user, expenditureCategory1);
		expenditureUserCategory2 = saveUserCategory(user, expenditureCategory2);
		UserCategory expenditureUserCategory3 = saveUserCategory(user, expenditureCategory3);

		// 2022.07월달에 category1으로 1건의 지출등록
		iterateFixture(1, (i) -> saveExpenditure(expenditureUserCategory1, AMOUNT, of(2022, 7, i, 0, 0, 0)));
		// 2022.07월달에 category2으로 2건의 지출등록
		iterateFixture(2, (i) -> saveExpenditure(expenditureUserCategory2, AMOUNT, of(2022, 7, i, 0, 0, 0)));
		// 2022.07월달에 category3으로 3건의 지출등록
		iterateFixture(3, (i) -> saveExpenditure(expenditureUserCategory3, AMOUNT, of(2022, 7, i, 0, 0, 0)));
		// 2022.06월달에 category1으로 2건의 지출등록
		iterateFixture(2, (i) -> saveExpenditure(expenditureUserCategory1, AMOUNT, of(2022, 6, i, 0, 0, 0)));
		// 2021.07월달에 category1으로 2건의 지출등록
		iterateFixture(2, (i) -> saveExpenditure(expenditureUserCategory1, AMOUNT, of(2022, 2, i, 0, 0, 0)));
	}
}
