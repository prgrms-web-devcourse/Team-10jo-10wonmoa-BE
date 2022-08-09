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
public enum CreateOrUpdateBudgetRequestDoc {
	REGISTER_DATE(STRING, "registerDate", "예산 등록 날짜"),
	AMOUNT(NUMBER, "amount", "예산 금액"),
	USER_CATEGORY_ID(NUMBER, "userCategoryId", "유저 카테고리 ID");

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
			USER_CATEGORY_ID.getFieldDescriptor()
		);
	}
}
