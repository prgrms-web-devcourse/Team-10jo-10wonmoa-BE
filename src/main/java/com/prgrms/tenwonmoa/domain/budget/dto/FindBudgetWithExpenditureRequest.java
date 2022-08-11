package com.prgrms.tenwonmoa.domain.budget.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FindBudgetWithExpenditureRequest {
	@NotNull
	@Min(value = 1900, message = "연도의 최솟값은 1900입니다.")
	Integer year;

	@Range(min = 1, max = 12, message = "월은 1~12 범위만 입력할 수 있습니다.")
	Integer month;
}
