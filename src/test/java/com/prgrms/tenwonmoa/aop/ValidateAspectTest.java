package com.prgrms.tenwonmoa.aop;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.prgrms.tenwonmoa.common.BaseControllerIntegrationTest;
import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.dto.income.UpdateIncomeRequest;
import com.prgrms.tenwonmoa.domain.accountbook.repository.ExpenditureRepository;
import com.prgrms.tenwonmoa.domain.accountbook.repository.IncomeRepository;
import com.prgrms.tenwonmoa.domain.accountbook.service.ExpenditureService;
import com.prgrms.tenwonmoa.domain.accountbook.service.IncomeService;
import com.prgrms.tenwonmoa.domain.accountbook.service.IncomeTotalService;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.repository.CategoryRepository;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;
import com.prgrms.tenwonmoa.exception.UnauthorizedUserException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Import(ValidateAspect.class)
class ValidateAspectTest extends BaseControllerIntegrationTest {
	@Autowired
	private IncomeTotalService incomeTotalService;
	@Autowired
	private IncomeService incomeService;
	@Autowired
	private ExpenditureService expenditureService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ExpenditureRepository expenditureRepository;
	@Autowired
	private UserCategoryRepository userCategoryRepository;
	@Autowired
	private IncomeRepository incomeRepository;
	@Autowired
	private CategoryRepository categoryRepository;

	private User loginUser;
	private Category category;
	private UserCategory userCategory;
	private Income income;

	private final UpdateIncomeRequest updateIncomeRequest = new UpdateIncomeRequest(LocalDateTime.now(),
		2000L,
		"updateContent",
		2L);

	@BeforeEach
	void init() throws Exception {
		registerUserAndLogin();
		loginUser = userRepository.findByEmail("testuser@gmail.com").get();
		category = categoryRepository.save(createIncomeCategory());
		userCategory = userCategoryRepository.save(createUserCategory(loginUser, category));
		income = incomeRepository.save(createIncome(userCategory));
	}

	@AfterEach
	void clear() {
		incomeRepository.deleteAllInBatch();
		expenditureRepository.deleteAllInBatch();
		userCategoryRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
		categoryRepository.deleteAllInBatch();
	}

	@Test
	void 다른유저의_수입정보삭제_유효성검사_성공() {
		assertThatThrownBy(() ->
			incomeTotalService.deleteIncome(loginUser.getId() + 1, income.getId()))
			.isInstanceOf(UnauthorizedUserException.class);
	}

	@Test
	void 존재하지않는_수입아이디삭제_유효성검사_성공() {
		assertThatThrownBy(() ->
			incomeTotalService.deleteIncome(loginUser.getId(), income.getId() + 1))
			.isInstanceOf(NoSuchElementException.class);
	}

	@Test
	void 다른유저_수입정보_업데이트_유효성검사_성공() {
		assertThatThrownBy(() ->
			incomeTotalService.updateIncome(loginUser.getId() + 1, income.getId(), updateIncomeRequest))
			.isInstanceOf(UnauthorizedUserException.class);
	}

	@Test
	void 다른유저_수입상세조회_유효성검사_성공() {
		assertThatThrownBy(() ->
			incomeService.findIncome(loginUser.getId() + 1, income.getId()))
			.isInstanceOf(UnauthorizedUserException.class);
	}

	@Test
	@Disabled
	void 같은조건_어노테이션이없다면_유효성검사_수행하지않음() {
		Expenditure expenditure = expenditureRepository.save(createExpenditure(userCategory));
		log.info("logging level debug 변경 후, 로그가 안찍히는지 확인");
		expenditureService.findExpenditure(loginUser.getId(), expenditure.getId());
	}
}