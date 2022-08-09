package com.prgrms.tenwonmoa.domain.budget.service;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.mockito.BDDMockito.*;

import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prgrms.tenwonmoa.domain.budget.Budget;
import com.prgrms.tenwonmoa.domain.budget.repository.BudgetRepository;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.service.UserCategoryService;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.service.UserService;

@DisplayName("예산 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {
	@Mock
	private BudgetRepository budgetRepository;
	@Mock
	private UserService userService;
	@Mock
	private UserCategoryService userCategoryService;
	@InjectMocks
	private BudgetService budgetService;

	private User user = createUser();
	private Category category = createExpenditureCategory();
	private UserCategory userCategory = createUserCategory(user, category);

	private YearMonth now = YearMonth.now();
	private Long userId = 1L;

	List<Budget> budgets = List.of(new Budget(100L, now, user, userCategory));

	@Test
	void 월별_예산조회_성공() {
		given(budgetRepository.findByUserIdAndRegisterDate(userId, now)).willReturn(budgets);
		// when
		budgetService.findByUserIdAndRegisterDate(userId, now);
		verify(budgetRepository).findByUserIdAndRegisterDate(userId, now);
	}
}
