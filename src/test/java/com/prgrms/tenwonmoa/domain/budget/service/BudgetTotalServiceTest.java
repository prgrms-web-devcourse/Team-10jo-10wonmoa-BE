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
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.service.UserCategoryService;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.service.UserService;

@DisplayName("예산 서비스 Total 테스트")
@ExtendWith(MockitoExtension.class)
class BudgetTotalServiceTest {
	private static final CreateOrUpdateBudgetRequest REQUEST = new CreateOrUpdateBudgetRequest(
		1000L, YearMonth.of(2020, 01), 1L);
	private static final String[] CATEGORIES = {"패션/미용", "교통/차량", "건강"};
	private static final Long USER_ID = 1L;
	private static User user = createUser();
	private static List<FindBudgetByRegisterDate> budgets = List.of(
		new FindBudgetByRegisterDate(1L, CATEGORIES[0], 100000L),
		new FindBudgetByRegisterDate(2L, CATEGORIES[1], 50000L),
		new FindBudgetByRegisterDate(3L, CATEGORIES[2], 50000L)
	);
	private static List<FindBudgetData> findBudgetDataList = List.of(
		new FindBudgetData(1L, "ct1", 1000L),
		new FindBudgetData(2L, "ct2", 2000L)
	);
	private static Map<Long, Long> expenditures = Map.of(
		1L, 122860L,
		2L, 46700L,
		3L, 43700L
	);
	private static UserCategory mockUserCategory = mock(UserCategory.class);
	private static User mockUser = mock(User.class);
	private static Budget mockBudget = mock(Budget.class);
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
	@Test
	void 예산_생성_성공() {
		given(userCategoryService.findById(anyLong())).willReturn(mockUserCategory);
		given(mockUserCategory.getUser()).willReturn(mockUser);
		doNothing().when(mockUser).validateLoginUser(anyLong());
		given(userService.findById(anyLong())).willReturn(user);
		given(budgetRepository.findByUserCategoryIdAndRegisterDate(anyLong(), any(YearMonth.class)))
			.willReturn(Optional.empty());
		// when
		budgetTotalService.createOrUpdateBudget(USER_ID, REQUEST);
		assertAll(
			() -> verify(userCategoryService).findById(anyLong()),
			() -> verify(userService).findById(anyLong()),
			() -> verify(budgetRepository).findByUserCategoryIdAndRegisterDate(anyLong(), any(YearMonth.class)),
			() -> verify(budgetRepository).save(any(Budget.class))
		);
	}

	@Test
	void 예산_생성_업데이트처리되는경우() {
		given(userCategoryService.findById(anyLong())).willReturn(mockUserCategory);
		given(mockUserCategory.getUser()).willReturn(mockUser);
		doNothing().when(mockUser).validateLoginUser(anyLong());
		given(userService.findById(anyLong())).willReturn(mockUser);
		given(budgetRepository.findByUserCategoryIdAndRegisterDate(anyLong(), any(YearMonth.class)))
			.willReturn(Optional.of(mockBudget));
		// when
		budgetTotalService.createOrUpdateBudget(USER_ID, REQUEST);
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
		assertThatThrownBy(() -> budgetTotalService.createOrUpdateBudget(USER_ID, REQUEST))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage(USER_CATEGORY_NOT_FOUND.getMessage());
	}

	@Test
	void 월별_유저카테고리별_예산조회() {
		given(budgetQueryRepository.searchUserCategoriesWithBudget(anyLong(), any(YearMonth.class)))
			.willReturn(findBudgetDataList);
		// when
		budgetTotalService.searchUserCategoriesWithBudget(USER_ID, YearMonth.now());
		// then
		verify(budgetQueryRepository).searchUserCategoriesWithBudget(anyLong(), any(YearMonth.class));
	}

	@Test
	void 월연별_예산조회_성공_행위검증() {
		given(budgetQueryRepository.searchExpendituresExistBudget(anyLong(), anyInt(), anyInt()))
			.willReturn(expenditures);
		given(budgetQueryRepository.searchBudgetByRegisterDate(anyLong(), anyInt(), anyInt()))
			.willReturn(budgets);
		// when
		budgetTotalService.searchBudgetWithExpenditure(USER_ID, 2022, 10);
		// then
		verify(budgetQueryRepository).searchExpendituresExistBudget(anyLong(), anyInt(), anyInt());
		verify(budgetQueryRepository).searchBudgetByRegisterDate(anyLong(), anyInt(), anyInt());
	}

	@Test
	void 월별_예산조회_성공_상태검증() {
		given(budgetQueryRepository.searchExpendituresExistBudget(anyLong(), anyInt(), anyInt()))
			.willReturn(expenditures);
		given(budgetQueryRepository.searchBudgetByRegisterDate(anyLong(), anyInt(), anyInt()))
			.willReturn(budgets);
		// when
		FindBudgetWithExpenditureResponse result = budgetTotalService.searchBudgetWithExpenditure(
			USER_ID, 2022, 10);
		// then
		long expenditureSum = expenditures.keySet().stream().mapToLong(key -> expenditures.get(key)).sum();
		long budgetSum = budgets.stream().mapToLong(FindBudgetByRegisterDate::getAmount).sum();
		assertThat(result.getRegisterDate()).isEqualTo("2022-10");
		assertThat(result.getAmount()).isEqualTo(budgetSum);
		assertThat(result.getExpenditure()).isEqualTo(expenditureSum);
		assertThat(result.getPercent()).isEqualTo(106L);
		assertThat(result.getBudgets()).extracting((data) -> data.getUserCategoryId(),
				(data) -> data.getCategoryName(),
				(data) -> data.getAmount(),
				(data) -> data.getExpenditure(),
				(data) -> data.getPercent())
			.contains(tuple(1L, CATEGORIES[0], 100000L, 122860L, 122L),
				tuple(2L, CATEGORIES[1], 50000L, 46700L, 93L),
				tuple(3L, CATEGORIES[2], 50000L, 43700L, 87L));
	}

	@Test
	void 연별_예산조회_성공_상태검증() {
		given(budgetQueryRepository.searchExpendituresExistBudget(anyLong(), anyInt(), isNull()))
			.willReturn(expenditures);
		given(budgetQueryRepository.searchBudgetByRegisterDate(anyLong(), anyInt(), isNull()))
			.willReturn(budgets);
		// when
		FindBudgetWithExpenditureResponse result = budgetTotalService.searchBudgetWithExpenditure(
			USER_ID, 2022, null);
		// then
		assertThat(result.getRegisterDate()).isEqualTo("2022");
	}

	@Test
	void 예산0원_지출발생한경우_퍼센트는_지출금액으로반환() {
		List<FindBudgetByRegisterDate> zeroBudgets = List.of(
			new FindBudgetByRegisterDate(1L, CATEGORIES[0], 0L),
			new FindBudgetByRegisterDate(2L, CATEGORIES[1], 0L),
			new FindBudgetByRegisterDate(3L, CATEGORIES[2], 0L)
		);
		given(budgetQueryRepository.searchExpendituresExistBudget(anyLong(), anyInt(), isNull()))
			.willReturn(expenditures);
		given(budgetQueryRepository.searchBudgetByRegisterDate(anyLong(), anyInt(), isNull()))
			.willReturn(zeroBudgets);
		// when
		FindBudgetWithExpenditureResponse result = budgetTotalService.searchBudgetWithExpenditure(
			USER_ID, 2022, null);
		// then
		assertThat(result.getBudgets()).extracting((data) -> data.getUserCategoryId(),
				(data) -> data.getCategoryName(),
				(data) -> data.getAmount(),
				(data) -> data.getExpenditure(),
				(data) -> data.getPercent())
			.contains(tuple(1L, CATEGORIES[0], 0L, 122860L, 122860L),
				tuple(2L, CATEGORIES[1], 0L, 46700L, 46700L),
				tuple(3L, CATEGORIES[2], 0L, 43700L, 43700L));
	}
}
