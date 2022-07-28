package com.prgrms.tenwonmoa.common.fixture;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;

import java.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.prgrms.tenwonmoa.common.RepositoryTest;
import com.prgrms.tenwonmoa.common.annotation.CustomDataJpaTest;
import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.repository.ExpenditureRepository;
import com.prgrms.tenwonmoa.domain.accountbook.repository.IncomeRepository;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.repository.CategoryRepository;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

public class RepositoryFixture extends RepositoryTest {

	public User saveUser() {
		return save(createUser());
	}

	public Category saveCategory() {
		return save(createCategory());
	}

	public UserCategory saveUserCategory() {
		return save(new UserCategory(saveUser(), saveCategory()));
	}

	public Income saveIncome() {
		UserCategory userCategory = saveUserCategory();
		return save(new Income(LocalDate.now(),
			1000L,
			"content",
			userCategory.getCategory().getName(),
			userCategory.getUser(),
			userCategory));
	}
}
