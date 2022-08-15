package com.prgrms.tenwonmoa.domain.category.controller;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.prgrms.tenwonmoa.domain.category.dto.FindCategoryResponse;
import com.prgrms.tenwonmoa.domain.category.service.FindUserCategoryService;
import com.prgrms.tenwonmoa.domain.category.service.UserCategoryService;
import com.prgrms.tenwonmoa.domain.user.User;
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
	public ResponseEntity<FindCategoryResponse> findUserCategories(
		@RequestParam @NotEmpty String kind,
		@AuthenticationPrincipal Long userId) {
		return ResponseEntity.ok(findUserCategoryService.findUserCategories(userId, kind));
	}

	@PostMapping
	public ResponseEntity<Map<String, Long>> createUserCategory(
		@RequestBody @Valid CreateCategoryRequest request,
		@AuthenticationPrincipal Long userId) {
		Long userCategoryId = userCategoryService.createUserCategory(
			userService.findById(userId), request.getCategoryType(), request.getName());
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", userCategoryId));
	}

	@PatchMapping("/{userCategoryId}")
	public ResponseEntity<Map<String, String>> updateUserCategory(
		@PathVariable Long userCategoryId,
		@RequestBody Map<String, Object> updateRequest,
		@AuthenticationPrincipal Long userId) {
		String updatedName = userCategoryService.updateName(userService.findById(userId), userCategoryId,
			(String)updateRequest.get("name"));
		return ResponseEntity.ok(Map.of("name", updatedName));
	}

	@DeleteMapping("/{userCategoryId}")
	public ResponseEntity<Void> deleteUserCategory(
		@PathVariable Long userCategoryId, @AuthenticationPrincipal Long userId) {
		User user = userService.findById(userId);
		userCategoryService.deleteUserCategory(user, userCategoryId);
		return ResponseEntity.noContent().build();
	}

}
