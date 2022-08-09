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
public enum FindBudgetDataDoc {
	ID(NUMBER, "id", "예산 ID"),
	CATEGORY_NAME(STRING, "categoryName", "카테고리 이름"),
	AMOUNT(NUMBER, "amount", "예산 금액");

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
			ID.getFieldDescriptor(),
			CATEGORY_NAME.getFieldDescriptor(),
			AMOUNT.getFieldDescriptor()
		);
	}
}
