package com.prgrms.tenwonmoa.domain.accountbook.service;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static com.prgrms.tenwonmoa.domain.accountbook.dto.FindAccountBookResponse.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prgrms.tenwonmoa.domain.accountbook.AccountBookConst;
import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindAccountBookResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.service.SearchAccountBookCmd;
import com.prgrms.tenwonmoa.domain.accountbook.repository.SearchAccountBookRepository;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;
import com.prgrms.tenwonmoa.domain.user.User;

@DisplayName("가계부 검색 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class SearchAccountBookServiceTest {

	@Mock
	private SearchAccountBookRepository accountBookRepository;

	@InjectMocks
	private SearchAccountBookService accountBookService;

	private final User user = createUser();

	private final UserCategory foodCategory = new UserCategory(createUser(),
		new Category("식비", CategoryType.EXPENDITURE));

	private final UserCategory cultureCategory = new UserCategory(createUser(),
		new Category("문화생활", CategoryType.EXPENDITURE));

	private final UserCategory salaryCategory = new UserCategory(createUser(), new Category("월급", CategoryType.INCOME));

	private final Long userId = 1L;

	@Test
	void 가계부_검색_성공() {
		//given
		Income latestIncome = new Income(LocalDateTime.now().minusHours(1L), 30000L,
			"용돈", salaryCategory.getCategoryName(), user, salaryCategory);

		Expenditure secondExpenditure = new Expenditure(LocalDateTime.now().minusHours(5L), 10000L,
			"점심", foodCategory.getCategoryName(), user, foodCategory);

		Expenditure thirdExpenditure = new Expenditure(LocalDateTime.now().minusDays(1L), 20000L,
			"영화", cultureCategory.getCategoryName(), user, cultureCategory);

		Expenditure fourthExpenditure = new Expenditure(LocalDateTime.now().minusDays(2L), 30000L,
			"문화생활", cultureCategory.getCategoryName(), user, cultureCategory);

		Income fifthIncome = new Income(LocalDateTime.now().minusDays(3L), 100000L,
			"월급", salaryCategory.getCategoryName(), user, salaryCategory);

		SearchAccountBookCmd cmd = SearchAccountBookCmd.of("1,2,3", 0L, 1_000_000_000L,
			AccountBookConst.LEFT_MOST_REGISTER_DATE, AccountBookConst.RIGHT_MOST_REGISTER_DATE, "");
		PageCustomRequest pageRequest = new PageCustomRequest(1, 3);

		given(accountBookRepository.searchExpenditures(cmd.getMinPrice(), cmd.getMaxPrice(), cmd.getStart(),
			cmd.getEnd(), cmd.getContent(), cmd.getCategories(), userId, pageRequest))
			.willReturn(List.of(secondExpenditure, thirdExpenditure, fourthExpenditure));

		given(accountBookRepository.searchIncomes(cmd.getMinPrice(), cmd.getMaxPrice(), cmd.getStart(),
			cmd.getEnd(), cmd.getContent(), cmd.getCategories(), userId, pageRequest))
			.willReturn(List.of(latestIncome, fifthIncome));

		//when
		FindAccountBookResponse findAccountBookResponse =
			accountBookService.searchAccountBooks(userId, cmd, pageRequest);

		//then
		assertThat(findAccountBookResponse.getResults().size()).isEqualTo(pageRequest.getSize());
		assertThat(findAccountBookResponse.getResults())
			.extracting(Result::getCategoryName)
			.containsExactlyInAnyOrder(
				latestIncome.getCategoryName(),
				secondExpenditure.getCategoryName(),
				thirdExpenditure.getCategoryName());

		assertThat(findAccountBookResponse.getIncomeSum()).isEqualTo(latestIncome.getAmount());
		assertThat(findAccountBookResponse.getExpenditureSum())
			.isEqualTo(secondExpenditure.getAmount() + thirdExpenditure.getAmount());
	}

}
