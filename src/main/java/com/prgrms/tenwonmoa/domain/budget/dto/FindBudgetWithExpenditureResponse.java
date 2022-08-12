package com.prgrms.tenwonmoa.domain.budget.dto;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FindBudgetWithExpenditureResponse {

	private String registerDate;
	private Long amount;
	private Long expenditure;
	private Long percent;
	List<FindBudgetByRegisterDate> budgets;

	public FindBudgetWithExpenditureResponse(String registerDate, Long amount, Long expenditure, Long percent,
		List<FindBudgetByRegisterDate> budgets) {
		this.registerDate = registerDate;
		this.amount = amount;
		this.expenditure = expenditure;
		this.percent = percent;
		this.budgets = budgets;
	}
}
