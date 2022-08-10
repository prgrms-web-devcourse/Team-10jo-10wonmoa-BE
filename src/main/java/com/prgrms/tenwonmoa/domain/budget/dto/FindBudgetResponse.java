package com.prgrms.tenwonmoa.domain.budget.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class FindBudgetResponse {
	private List<FindBudgetData> budgets;

	public FindBudgetResponse(List<FindBudgetData> budgets) {
		this.budgets = budgets;
	}
}
