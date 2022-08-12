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
public enum FindBudgetWithExpenditureResponseDoc {

	REGISTER_DATE(STRING, "registerDate", "예산 등록일"),
	AMOUNT(NUMBER, "amount", "예산 총 금액"),
	EXPENDITURE(NUMBER, "expenditure", "지출 총 금액"),
	PERCENT(NUMBER, "percent", "사용량"),
	BUDGETS(ARRAY, "budgets[].", "예산 지출내역");

	private final JsonFieldType type;
	private final String field;
	private final String description;

	private FieldDescriptor getFieldDescriptor() {
		return fieldWithPath(this.getField())
			.type(this.getType())
			.description(this.getDescription());
	}

	public static List<FieldDescriptor> fieldDescriptors() {
		return List.of(
			REGISTER_DATE.getFieldDescriptor(),
			AMOUNT.getFieldDescriptor(),
			EXPENDITURE.getFieldDescriptor(),
			PERCENT.getFieldDescriptor()
		);
	}
}
