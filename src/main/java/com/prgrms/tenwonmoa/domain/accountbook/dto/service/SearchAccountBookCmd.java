package com.prgrms.tenwonmoa.domain.accountbook.dto.service;

import static com.google.common.base.Preconditions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

	private LocalDateTime start;

	private LocalDateTime end;

	private String content;

	public static SearchAccountBookCmd of(String categoryString, Long minPrice, Long maxPrice, LocalDate start,
		LocalDate end, String content) {

		String[] categoryIds = categoryString.split(",");
		List<Long> categories = Arrays.stream(categoryIds).map(Long::valueOf).collect(Collectors.toList());

		minPrice = minPrice == null ? AccountBookConst.AMOUNT_MIN : minPrice;
		maxPrice = maxPrice == null ? AccountBookConst.AMOUNT_MAX : maxPrice;
		start = start == null ? AccountBookConst.LEFT_MOST_REGISTER_DATE : start;
		end = end == null ? AccountBookConst.RIGHT_MOST_REGISTER_DATE : end;

		checkArgument(minPrice <= maxPrice, "최소값은 최대값 보다 작아야 합니다");
		checkArgument(start.compareTo(end) <= 0, "시작일은 종료일 전이여야 합니다");

		return new SearchAccountBookCmd(categories, minPrice, maxPrice, start.atTime(LocalTime.MIN),
			end.atTime(LocalTime.MAX), content);
	}

}
