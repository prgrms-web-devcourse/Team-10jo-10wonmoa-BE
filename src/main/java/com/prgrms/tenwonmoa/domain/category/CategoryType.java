package com.prgrms.tenwonmoa.domain.category;

public enum CategoryType {
	INCOME, EXPENDITURE;

	public static boolean isExpenditure(CategoryType type) {
		return type == EXPENDITURE;
	}
}
