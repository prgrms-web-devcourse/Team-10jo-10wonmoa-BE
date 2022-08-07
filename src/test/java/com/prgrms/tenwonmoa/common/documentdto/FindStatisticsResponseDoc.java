package com.prgrms.tenwonmoa.common.documentdto;

import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FindStatisticsResponseDoc {
	YEAR(NUMBER, "year", "년도"),
	MONTH(NUMBER, "month", "월"),
	MONTH_NULL(NULL, "month", "월"),
	INCOME_TOTAL_SUM(NUMBER, "incomeTotalSum", "수입 총 합계"),
	EXPENDITURE_TOTAL_SUM(NUMBER, "expenditureTotalSum", "지출 총 합계"),
	INCOMES(ARRAY, "incomes[].", "수입 리스트"),
	EXPENDITURES(ARRAY, "expenditures[].", "지출 리스트");

	private final JsonFieldType type;
	private final String field;
	private final String description;

	private FieldDescriptor getFieldDescriptor() {
		return fieldWithPath(this.getField())
			.type(this.getType())
			.description(this.getDescription());
	}

	public static List<FieldDescriptor> fieldDescriptorsYear() {
		return List.of(
			YEAR.getFieldDescriptor(),
			MONTH_NULL.getFieldDescriptor(),
			INCOME_TOTAL_SUM.getFieldDescriptor(),
			EXPENDITURE_TOTAL_SUM.getFieldDescriptor()
		);
	}

	public static List<FieldDescriptor> fieldDescriptorsMonth() {
		return List.of(
			YEAR.getFieldDescriptor(),
			MONTH.getFieldDescriptor(),
			INCOME_TOTAL_SUM.getFieldDescriptor(),
			EXPENDITURE_TOTAL_SUM.getFieldDescriptor()
		);
	}


}
