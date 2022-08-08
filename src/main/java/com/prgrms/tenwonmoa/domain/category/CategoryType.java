package com.prgrms.tenwonmoa.domain.category;

public enum CategoryType {
	INCOME, EXPENDITURE;

	public static boolean isExpenditure(CategoryType type) {
		return type == EXPENDITURE;
	}

	public static boolean isIncome(CategoryType type) {
		return type == INCOME;
	}
}
