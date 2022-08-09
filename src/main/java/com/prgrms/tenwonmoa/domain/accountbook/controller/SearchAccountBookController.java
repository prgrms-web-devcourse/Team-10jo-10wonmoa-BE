package com.prgrms.tenwonmoa.domain.accountbook.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.tenwonmoa.domain.accountbook.dto.AccountBookItem;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindAccountBookResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.service.SearchAccountBookCmd;
import com.prgrms.tenwonmoa.domain.accountbook.service.SearchAccountBookService;
import com.prgrms.tenwonmoa.domain.category.service.FindUserCategoryService;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account-book")
public class SearchAccountBookController {

	private static final String CATEGORY_DELIMITER = ",";
	private final SearchAccountBookService accountBookService;

	private final FindUserCategoryService userCategoryService;

	@GetMapping("/search")
	public ResponseEntity<FindAccountBookResponse<AccountBookItem>> searchAccountBooks(
		@AuthenticationPrincipal Long userId,
		@RequestParam(defaultValue = "") String categories,
		@RequestParam(name = "minprice", required = false) Long minPrice,
		@RequestParam(name = "maxprice", required = false) Long maxPrice,
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
		@RequestParam(defaultValue = "") String content,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "1") int page) {

		categories = categories.isEmpty() ? getAllCategories(userId) : categories;

		PageCustomRequest pageRequest = new PageCustomRequest(page, size);

		FindAccountBookResponse<AccountBookItem> response = accountBookService.searchAccountBooks(userId,
			SearchAccountBookCmd.of(categories, minPrice, maxPrice, start, end, content), pageRequest);

		return ResponseEntity.ok(response);
	}

	private String getAllCategories(Long userId) {
		List<Long> allUserCategories = userCategoryService.findAllUserCategoryIds(userId);
		String[] userCategoryStringArr = allUserCategories.stream()
			.map(String::valueOf)
			.collect(Collectors.toList())
			.toArray(String[]::new);

		return String.join(CATEGORY_DELIMITER, userCategoryStringArr);
	}
}
