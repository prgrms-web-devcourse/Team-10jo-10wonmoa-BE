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
public enum ErrorResponseDoc {
	MESSAGES(ARRAY, "messages", "예외 메시지"),
	STATUS(NUMBER, "status", "에러 코드");

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
			MESSAGES.getFieldDescriptor(),
			STATUS.getFieldDescriptor()
		);
	}
}
