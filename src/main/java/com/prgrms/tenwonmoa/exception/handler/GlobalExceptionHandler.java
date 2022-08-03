package com.prgrms.tenwonmoa.exception.handler;

import static org.springframework.http.HttpStatus.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.prgrms.tenwonmoa.exception.AlreadyExistException;
import com.prgrms.tenwonmoa.exception.UnauthorizedUserException;
import com.prgrms.tenwonmoa.exception.response.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	// 405 : Method Not Allowed
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
		HttpRequestMethodNotSupportedException exception) {
		log.error(exception.getMessage(), exception);
		ErrorResponse errorResponse = new ErrorResponse(List.of(exception.getMessage()), METHOD_NOT_ALLOWED.value());
		return ResponseEntity
			.status(METHOD_NOT_ALLOWED.value())
			.body(errorResponse);
	}

	// 400 : NotFound - 잘못된 요청
	// Client의 잘못된 요청으로 인한 에러 처리
	@ExceptionHandler({IllegalArgumentException.class, AlreadyExistException.class})
	public ResponseEntity<ErrorResponse> handleClientBadRequest(RuntimeException exception) {
		log.info(exception.getMessage(), exception);
		ErrorResponse errorResponse = new ErrorResponse(List.of(exception.getMessage()), BAD_REQUEST.value());
		return ResponseEntity
			.status(BAD_REQUEST.value())
			.body(errorResponse);
	}

	// 공격 or 버그
	@ExceptionHandler({NoSuchElementException.class, NullPointerException.class})
	public ResponseEntity<ErrorResponse> handleBug(RuntimeException exception) {
		log.error(exception.getMessage(), exception);
		ErrorResponse errorResponse = new ErrorResponse(List.of(exception.getMessage()), BAD_REQUEST.value());
		return ResponseEntity
			.status(BAD_REQUEST.value())
			.body(errorResponse);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleAlreadyExistException(MethodArgumentNotValidException exception) {
		BindingResult bindingResult = exception.getBindingResult();
		List<String> errors = bindingResult.getAllErrors()
			.stream()
			.map(DefaultMessageSourceResolvable::getDefaultMessage)
			.collect(Collectors.toList());

		ErrorResponse errorResponse = new ErrorResponse(errors, BAD_REQUEST.value());

		return ResponseEntity
			.status(BAD_REQUEST)
			.body(errorResponse);
	}

	@ExceptionHandler({UnauthorizedUserException.class})
	public ResponseEntity<ErrorResponse> handleForbidden(Exception exception) {
		log.error(exception.getMessage(), exception);
		ErrorResponse errorResponse = new ErrorResponse(List.of(exception.getMessage()), FORBIDDEN.value());
		return ResponseEntity
			.status(FORBIDDEN.value())
			.body(errorResponse);
	}

}
