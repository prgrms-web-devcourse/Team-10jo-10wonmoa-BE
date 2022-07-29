package com.prgrms.tenwonmoa.domain.accountbook.repository;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.repository.CategoryRepository;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;

@SpringBootTest
@Transactional
@DisplayName("가계부(AccountBook) 조회 레포지토리 테스트")
class AccountBookQueryRepositoryTest {

	@Autowired
	private EntityManager em;

	@Autowired
	private AccountBookQueryRepository accountBookQueryRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private UserCategoryRepository userCategoryRepository;

	@Autowired
	private ExpenditureRepository expenditureRepository;

	private User user;

	private Category category;

	private UserCategory userCategory;

	private Expenditure expenditure;

	@BeforeEach
	void setup() {
		user = userRepository.save(createUser());
		category = categoryRepository.save(createCategory());
		userCategory = userCategoryRepository.save(new UserCategory(user, category));
		expenditure = expenditureRepository.save(createExpenditure(userCategory));
	}

	@Test
	public void test() {
		accountBookQueryRepository.findById(expenditure.getId());
	}
}