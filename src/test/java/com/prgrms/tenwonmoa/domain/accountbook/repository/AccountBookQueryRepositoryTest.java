package com.prgrms.tenwonmoa.domain.accountbook.repository;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.prgrms.tenwonmoa.common.RepositoryTest;
import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;

@DisplayName("가계부(AccountBook) 조회 레포지토리 테스트")
class AccountBookQueryRepositoryTest extends RepositoryTest {

	@Autowired
	private EntityManager em;

	@Autowired
	private AccountBookQueryRepository accountBookQueryRepository;

	private User user;

	private Category category;

	private UserCategory userCategory;

	private Expenditure expenditure;

	@BeforeEach
	void setup() {
		user = save(createUser());
		category = save(createCategory());
		userCategory = save(new UserCategory(user, category));
		expenditure = save(createExpenditure(userCategory));
	}

	@Test
	public void test() {
		Optional<Expenditure> empty = accountBookQueryRepository.findById(0L);
		Optional<Expenditure> present = accountBookQueryRepository.findById(expenditure.getId());

		assertThat(empty).isEmpty();
		assertThat(present).isNotEmpty();
	}
}
