package com.prgrms.tenwonmoa.domain.category.dto;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateCategoryRequest {

	@NotEmpty(message = "카테고리 타입이 필요합니다")
	private String categoryType;

	@NotEmpty(message = "카테고리 이름을 필요합니다")
	private String name;

}
