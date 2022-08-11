package com.prgrms.tenwonmoa.domain.budget.dto;

import com.prgrms.tenwonmoa.domain.budget.Budget;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FindBudgetData {
	private Long id;
	private String categoryName;
	private Long amount;

	public FindBudgetData(Long id, String categoryName, Long amount) {
		this.id = id;
		this.categoryName = categoryName;
		this.amount = amount;
	}

	public static FindBudgetData of(Budget budget) {
		return new FindBudgetData(
			budget.getId(),
			budget.getCategoryName(),
			budget.getAmount()
		);
	}
}
