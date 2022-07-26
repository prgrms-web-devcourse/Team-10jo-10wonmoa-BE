package com.prgrms.tenwonmoa.domain.accountbook.service;

import static java.util.Optional.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.accountbook.dto.CreateExpenditureRequest;
import com.prgrms.tenwonmoa.domain.accountbook.dto.CreateExpenditureResponse;
import com.prgrms.tenwonmoa.domain.accountbook.repository.ExpenditureRepository;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.category.repository.CategoryRepository;
import com.prgrms.tenwonmoa.domain.category.repository.UserCategoryRepository;
import com.prgrms.tenwonmoa.domain.user.User;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;
import com.prgrms.tenwonmoa.exception.message.Message;

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

	private User user = new User("jungki111@gmail,com", "password1234!", "개발자");

	private UserCategory userCategory = new UserCategory(user, new Category("식비", CategoryType.EXPENDITURE));

	private Category category = new Category("식비", CategoryType.EXPENDITURE);

	private Expenditure expenditure = new Expenditure(LocalDate.now(), 10000L, "피자", category.getName(), user,
		userCategory);

	@Nested
	@DisplayName("지출 생성 중")
	class CreateExpenditure {

		Long userId = 1L;
		CreateExpenditureRequest request = new CreateExpenditureRequest(LocalDate.now(), 10000L, "식비", 1L);

		@Test
		public void user가_없을_경우() {
			given(userRepository.findById(userId)).willThrow(
				new NoSuchElementException(Message.USER_NOT_FOUND.getMessage()));

			assertThatThrownBy(() -> expenditureService.createExpenditure(userId, any()))
				.isInstanceOf(NoSuchElementException.class)
				.hasMessage(Message.USER_NOT_FOUND.getMessage());
		}

		@Test
		public void user_Category가_없을_경우() {
			given(userRepository.findById(any())).willReturn(of(user));
			given(userCategoryRepository.findById(request.getUserCategoryId()))
				.willThrow(new NoSuchElementException(Message.USER_CATEGORY_NOT_FOUND.getMessage()));

			assertThatThrownBy(() -> expenditureService.createExpenditure(any(), request))
				.isInstanceOf(NoSuchElementException.class)
				.hasMessage(Message.USER_CATEGORY_NOT_FOUND.getMessage());

		}

		@Test
		public void category가_없을_경우() {
			given(userRepository.findById(any())).willReturn(ofNullable(user));
			given(userCategoryRepository.findById(request.getUserCategoryId()))
				.willReturn(of(userCategory));

			given(categoryRepository.findById(userCategory.getCategory().getId()))
				.willThrow(new NoSuchElementException(Message.CATEGORY_NOT_FOUND.getMessage()));

			assertThatThrownBy(() -> expenditureService.createExpenditure(any(), request))
				.isInstanceOf(NoSuchElementException.class)
				.hasMessage(Message.CATEGORY_NOT_FOUND.getMessage());
		}

		@Test
		public void 성공적으로_controller에_응답을_반환한다() {
			given(userRepository.findById(userId)).willReturn(ofNullable(user));
			given(userCategoryRepository.findById(request.getUserCategoryId()))
				.willReturn(of(userCategory));
			given(categoryRepository.findById(any()))
				.willReturn(of(category));
			given(expenditureRepository.save(any(Expenditure.class))).willReturn(expenditure);

			CreateExpenditureResponse response = expenditureService.createExpenditure(userId, request);

			then(expenditureRepository).should().save(any());
		}
	}

	@Nested
	@DisplayName("지출 수정 중")
	class UpdateExpenditure {

		@Test
		public void user_권한이_없을_경우() {

		}

		@Test
		public void 없는_userCategoryId일_경우() {

		}

		@Test
		public void 성공적으로_수정이_가능하다() {

		}

	}
}
