package com.prgrms.tenwonmoa.domain.budget.service;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static com.prgrms.tenwonmoa.exception.message.Message.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prgrms.tenwonmoa.domain.budget.Budget;
import com.prgrms.tenwonmoa.domain.budget.dto.CreateOrUpdateBudgetRequest;
import com.prgrms.tenwonmoa.domain.budget.dto.FindBudgetByRegisterDate;
import com.prgrms.tenwonmoa.domain.budget.dto.FindBudgetData;
import com.prgrms.tenwonmoa.domain.budget.dto.FindBudgetWithExpenditureResponse;
import com.prgrms.tenwonmoa.domain.budget.repository.BudgetQueryRepository;
import com.prgrms.tenwonmoa.domain.budget.repository.BudgetRepository;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.service.UserCategoryService;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.service.UserService;

@DisplayName("예산 서비스 Total 테스트")
@ExtendWith(MockitoExtension.class)
class BudgetTotalServiceTest {
	@Mock
	private BudgetRepository budgetRepository;
	@Mock
	private UserService userService;
	@Mock
	private UserCategoryService userCategoryService;
	@Mock
	private BudgetQueryRepository budgetQueryRepository;
	@InjectMocks
	private BudgetTotalService budgetTotalService;

	private User user = createUser();
	private Category category = createExpenditureCategory();
	private UserCategory userCategory = createUserCategory(user, category);
	private CreateOrUpdateBudgetRequest createOrUpdateBudgetRequest = new CreateOrUpdateBudgetRequest(
		1000L, YearMonth.of(2020, 01), userCategory.getId());
	private String[] categories = {"패션/미용", "교통/차량", "건강"};

	private List<FindBudgetData> findBudgetDataList = List.of(
		new FindBudgetData(1L, "ct1", 1000L),
		new FindBudgetData(2L, "ct2", 2000L)
	);

	private static final Long userId = 1L;

	private Map<String, Long> expenditures = Map.of(
		categories[0], 122860L,
		categories[1], 46700L,
		categories[2], 43700L
	);
	private List<FindBudgetByRegisterDate> budgets = List.of(
		new FindBudgetByRegisterDate(categories[0], 100000L),
		new FindBudgetByRegisterDate(categories[1], 50000L),
		new FindBudgetByRegisterDate(categories[2], 50000L)
	);

	@Test
	void 예산_생성_성공() {
		UserCategory mockUserCategory = mock(UserCategory.class);
		User mockUser = mock(User.class);
		given(userCategoryService.findById(any())).willReturn(mockUserCategory);
		given(mockUserCategory.getUser()).willReturn(mockUser);
		doNothing().when(mockUser).validateLoginUser(any());
		given(userService.findById(any())).willReturn(user);
		given(budgetRepository.findByUserCategoryIdAndRegisterDate(
			any(), any())).willReturn(Optional.empty());

		budgetTotalService.createOrUpdateBudget(user.getId(), createOrUpdateBudgetRequest);
		assertAll(
			() -> verify(userCategoryService).findById(any()),
			() -> verify(userService).findById(any()),
			() -> verify(budgetRepository).findByUserCategoryIdAndRegisterDate(any(), any()),
			() -> verify(budgetRepository).save(any())
		);
	}

	@Test
	void 예산_생성_업데이트처리되는경우() {
		Budget mockBudget = mock(Budget.class);
		UserCategory mockUserCategory = mock(UserCategory.class);
		User mockUser = mock(User.class);
		given(userCategoryService.findById(any())).willReturn(mockUserCategory);
		given(mockUserCategory.getUser()).willReturn(mockUser);
		doNothing().when(mockUser).validateLoginUser(any());
		given(userService.findById(any())).willReturn(mockUser);
		given(budgetRepository.findByUserCategoryIdAndRegisterDate(
			any(), any())).willReturn(Optional.of(mockBudget));

		budgetTotalService.createOrUpdateBudget(userId, createOrUpdateBudgetRequest);
		assertAll(
			() -> verify(userCategoryService).findById(any()),
			() -> verify(userService).findById(any()),
			() -> verify(budgetRepository).findByUserCategoryIdAndRegisterDate(any(), any()),
			() -> verify(budgetRepository, never()).save(any())
		);
	}

	@Test
	void 예산_생성실패_유저카테고리_없는경우() {
		given(userCategoryService.findById(any())).willThrow(
			new NoSuchElementException(USER_CATEGORY_NOT_FOUND.getMessage()));
		assertThatThrownBy(() -> budgetTotalService.createOrUpdateBudget(user.getId(), createOrUpdateBudgetRequest))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage(USER_CATEGORY_NOT_FOUND.getMessage());
	}

	@Test
	void 월별_유저카테고리별_예산조회() {
		YearMonth now = YearMonth.now();
		given(budgetQueryRepository.searchUserCategoriesWithBudget(any(), any())).willReturn(findBudgetDataList);
		// when
		budgetTotalService.searchUserCategoriesWithBudget(userId, now);
		// then
		verify(budgetQueryRepository).searchUserCategoriesWithBudget(userId, now);
	}

	@Test
	void 월연별_예산조회_성공_행위검증() {
		given(budgetQueryRepository.searchExpendituresExistBudget(any(), any(), any()))
			.willReturn(expenditures);
		given(budgetQueryRepository.searchBudgetByRegisterDate(any(), any(), any()))
			.willReturn(budgets);
		// when
		FindBudgetWithExpenditureResponse result = budgetTotalService.searchBudgetWithExpenditure(
			userId, 2022, 10);
		// then
		verify(budgetQueryRepository).searchExpendituresExistBudget(userId, 2022, 10);
		verify(budgetQueryRepository).searchBudgetByRegisterDate(userId, 2022, 10);
	}

	@Test
	void 월별_예산조회_성공_상태검증() {
		given(budgetQueryRepository.searchExpendituresExistBudget(any(), any(), any()))
			.willReturn(expenditures);
		given(budgetQueryRepository.searchBudgetByRegisterDate(any(), any(), any()))
			.willReturn(budgets);

		// when
		FindBudgetWithExpenditureResponse result = budgetTotalService.searchBudgetWithExpenditure(
			userId, 2022, 10);
		// then
		long expenditureSum = expenditures.keySet().stream().mapToLong(key -> expenditures.get(key)).sum();
		long budgetSum = budgets.stream().mapToLong(FindBudgetByRegisterDate::getAmount).sum();
		assertThat(result.getRegisterDate()).isEqualTo("2022-10");
		assertThat(result.getAmount()).isEqualTo(budgetSum);
		assertThat(result.getExpenditure()).isEqualTo(expenditureSum);
		assertThat(result.getPercent()).isEqualTo(106L);
		assertThat(result.getBudgets()).extracting((data) -> data.getCategoryName(),
				(data) -> data.getAmount(),
				(data) -> data.getExpenditure(),
				(data) -> data.getPercent())
			.contains(tuple(categories[0], 100000L, 122860L, 122L),
				tuple(categories[1], 50000L, 46700L, 93L),
				tuple(categories[2], 50000L, 43700L, 87L));
	}

	@Test
	void 연별_예산조회_성공_상태검증() {
		given(budgetQueryRepository.searchExpendituresExistBudget(any(), any(), any()))
			.willReturn(expenditures);
		given(budgetQueryRepository.searchBudgetByRegisterDate(any(), any(), any()))
			.willReturn(budgets);

		// when
		FindBudgetWithExpenditureResponse result = budgetTotalService.searchBudgetWithExpenditure(
			userId, 2022, null);
		// then
		assertThat(result.getRegisterDate()).isEqualTo("2022");
	}
}
