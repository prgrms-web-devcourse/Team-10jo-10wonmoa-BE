package com.prgrms.tenwonmoa.domain.budget.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FindBudgetByRegisterDate {
	private String categoryName;
	private Long amount;
	private Long expenditure;
	private Long percent;

	public FindBudgetByRegisterDate(String categoryName, Long amount) {
		this.categoryName = categoryName;
		this.amount = amount;
	}

	public void setExpenditure(Long expenditure) {
		this.expenditure = expenditure;
	}

	public void setPercent(Long percent) {
		this.percent = percent;
	}
}
