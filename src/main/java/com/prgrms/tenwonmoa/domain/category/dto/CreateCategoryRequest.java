package com.prgrms.tenwonmoa.domain.category.dto;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateCategoryRequest {

	@NotEmpty
	private String categoryType;

	@NotEmpty
	private String name;

}
