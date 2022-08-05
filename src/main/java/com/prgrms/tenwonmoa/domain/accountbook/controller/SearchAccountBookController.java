package com.prgrms.tenwonmoa.domain.accountbook.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.tenwonmoa.domain.accountbook.dto.SearchAccountBookResponse;
import com.prgrms.tenwonmoa.domain.accountbook.service.SearchAccountBookCmd;
import com.prgrms.tenwonmoa.domain.accountbook.service.SearchAccountBookService;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.dto.FindCategoryResponse;
import com.prgrms.tenwonmoa.domain.category.service.FindUserCategoryService;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account-book")
public class SearchAccountBookController {

	private final SearchAccountBookService accountBookService;

	private final FindUserCategoryService userCategoryService;

	@GetMapping("/search")
	public ResponseEntity<SearchAccountBookResponse> searchAccountBooks(
		@AuthenticationPrincipal Long userId,
		@RequestParam(defaultValue = "") String categories,
		@RequestParam(name = "minprice", required = false) Long minPrice,
		@RequestParam(name = "maxprice", required = false) Long maxPrice,
		@RequestParam(required = false) LocalDate start,
		@RequestParam(required = false) LocalDate end,
		@RequestParam(defaultValue = "") String content,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "1") int page) {

		categories = categories.isEmpty() ? getAllCategories(userId) : categories;

		SearchAccountBookResponse response = accountBookService.searchAccountBooks(userId,
			SearchAccountBookCmd.of(categories, minPrice, maxPrice, start, end, content),
			new PageCustomRequest(page, size));

		return ResponseEntity.ok(response);
	}

	private String getAllCategories(Long userId) {
		FindCategoryResponse expenditureCategories = userCategoryService.findUserCategories(userId,
			CategoryType.EXPENDITURE.name());
		FindCategoryResponse incomeCategories = userCategoryService.findUserCategories(userId,
			CategoryType.INCOME.name());

		List<Long> categoryIds = new ArrayList<>();
		expenditureCategories.getCategories().forEach(resp -> categoryIds.add(resp.getId()));
		incomeCategories.getCategories().forEach(resp -> categoryIds.add(resp.getId()));

		return String.join(",", categoryIds.stream()
			.map(String::valueOf)
			.collect(Collectors.toList())
			.toArray(String[]::new));
	}
}
