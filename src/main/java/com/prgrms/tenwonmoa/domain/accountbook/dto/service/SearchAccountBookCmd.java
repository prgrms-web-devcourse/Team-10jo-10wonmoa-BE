package com.prgrms.tenwonmoa.domain.accountbook.dto.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.prgrms.tenwonmoa.domain.accountbook.AccountBookConst;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class SearchAccountBookCmd {

	private List<Long> categories;

	private Long minPrice;

	private Long maxPrice;

	private LocalDate start;

	private LocalDate end;

	private String content;

	public static SearchAccountBookCmd of(String categoryString, Long minPrice, Long maxPrice, LocalDate start,
		LocalDate end, String content) {
		String[] categoryIds = categoryString.split(",");
		List<Long> categories = Arrays.stream(categoryIds).map(Long::getLong).collect(Collectors.toList());

		minPrice = minPrice == null ? AccountBookConst.AMOUNT_MIN : minPrice;
		maxPrice = maxPrice == null ? AccountBookConst.AMOUNT_MAX : maxPrice;
		start = start == null ? AccountBookConst.LEFT_MOST_REGISTER_DATE : start;
		end = end == null ? AccountBookConst.RIGHT_MOST_REGISTER_DATE : end;

		return new SearchAccountBookCmd(categories, minPrice, maxPrice, start, end, content);
	}

}
