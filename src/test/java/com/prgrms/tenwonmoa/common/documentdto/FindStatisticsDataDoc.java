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
public enum FindStatisticsDataDoc {
	NAME(STRING, "name", "카테고리 이름"),
	TOTAL(NUMBER, "total", "총 합"),
	PERCENT(NUMBER, "percent", "비율");

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
			NAME.getFieldDescriptor(),
			TOTAL.getFieldDescriptor(),
			PERCENT.getFieldDescriptor()
		);
	}
}
