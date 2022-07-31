package com.prgrms.tenwonmoa.domain.category.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.tenwonmoa.domain.category.service.UserCategoryService;
import com.prgrms.tenwonmoa.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class UserCategoryController {

	private final UserCategoryService userCategoryService;

	private final UserService userService;

	@DeleteMapping("/{userCategoryId}")
	public ResponseEntity<Void> deleteUserCategory(@PathVariable Long userCategoryId) {
		Long userId = 1L; // 추후 AuthenticationPrincipal 적용예정

		userCategoryService.deleteUserCategory(userService.findById(userId), userCategoryId);
		return ResponseEntity.noContent().build();
	}
}
