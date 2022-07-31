package com.prgrms.tenwonmoa.domain.category.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.tenwonmoa.domain.category.dto.CreateCategoryRequest;
import com.prgrms.tenwonmoa.domain.category.service.FindUserCategoryService;
import com.prgrms.tenwonmoa.domain.category.service.UserCategoryService;
import com.prgrms.tenwonmoa.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class UserCategoryController {

	private final UserCategoryService userCategoryService;

	private final FindUserCategoryService findUserCategoryService;

	private final UserService userService;

	@GetMapping
	public ResponseEntity findUserCategories(@RequestParam String kind) {
		return null;
	}

	@PostMapping
	public ResponseEntity createUserCategory(@RequestBody CreateCategoryRequest request) {
		return null;
	}

	@PatchMapping("/{userCategoryId}")
	public ResponseEntity updateUserCategory(Map<String, String> updateRequest) {
		return null;
	}

	@DeleteMapping("/{userCategoryId}")
	public ResponseEntity<Void> deleteUserCategory(@PathVariable Long userCategoryId) {
		Long userId = 1L; // 추후 AuthenticationPrincipal 적용예정

		userCategoryService.deleteUserCategory(userService.findById(userId), userCategoryId);
		return ResponseEntity.noContent().build();
	}

}
