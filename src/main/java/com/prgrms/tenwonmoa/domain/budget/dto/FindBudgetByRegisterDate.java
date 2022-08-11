package com.prgrms.tenwonmoa.domain.budget.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FindBudgetByRegisterDate {
	private Long userCategoryId;
	private String categoryName;
	private Long amount;
	private Long expenditure;
	private Long percent;

	public FindBudgetByRegisterDate(Long userCategoryId, String categoryName, Long amount) {
		this.userCategoryId = userCategoryId;
		this.categoryName = categoryName;
		this.amount = amount;
		this.expenditure = 0L;
		this.percent = 0L;
	}

	public void setExpenditure(Long expenditure) {
		this.expenditure = expenditure;
	}

	public void setPercent(Long percent) {
		this.percent = percent;
	}
}
