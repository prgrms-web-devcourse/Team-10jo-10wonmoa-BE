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
public enum FindIncomeResponseDoc {
	ID(NUMBER, "id", "수입 ID"),
	REGISTER_DATE(STRING, "registerDate", "수입 등록 날짜"),
	AMOUNT(NUMBER, "amount", "수입 금액"),
	CONTENT(STRING, "content", "내용"),
	CATEGORY_NAME(STRING, "categoryName", "카테고리 이름");

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
			REGISTER_DATE.getFieldDescriptor(),
			AMOUNT.getFieldDescriptor(),
			CONTENT.getFieldDescriptor(),
			CATEGORY_NAME.getFieldDescriptor()
		);
	}
}
