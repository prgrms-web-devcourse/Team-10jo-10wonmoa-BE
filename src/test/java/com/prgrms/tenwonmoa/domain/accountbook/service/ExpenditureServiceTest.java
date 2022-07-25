package com.prgrms.tenwonmoa.domain.accountbook.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prgrms.tenwonmoa.domain.accountbook.repository.ExpenditureRepository;
import com.prgrms.tenwonmoa.domain.category.repository.CategoryRepository;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;

@DisplayName("Expenditure(지출) 서비스 계층 단위 테스트")
@ExtendWith(MockitoExtension.class)
class ExpenditureServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserCategoryRepository userCategoryRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private ExpenditureRepository expenditureRepository;

	@InjectMocks
	private ExpenditureService expenditureService;

	@Nested
	class 지출_생성_중 {

		@Test
		public void User가_없을_경우() {

		}

		@Test
		public void User_Category가_없을_경우() {

		}

		@Test
		public void Category가_없을_경우() {

		}

	}

	@Test
	public void 성공적으로_controller에_응답을_반환한다() {

	}

}