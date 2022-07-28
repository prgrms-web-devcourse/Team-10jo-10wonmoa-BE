package com.prgrms.tenwonmoa.exception.handler;

import static org.springframework.http.HttpStatus.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.prgrms.tenwonmoa.exception.AlreadyExistException;
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
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<ErrorResponse> handleNotFoundException(NoSuchElementException exception) {
		log.error(exception.getMessage(), exception);
		ErrorResponse errorResponse = new ErrorResponse(List.of(exception.getMessage()), BAD_REQUEST.value());
		return ResponseEntity
			.status(BAD_REQUEST.value())
			.body(errorResponse);
	}

	@ExceptionHandler(AlreadyExistException.class)
	public ResponseEntity<ErrorResponse> handleAlreadyExistException(AlreadyExistException exception) {
		log.error(exception.getMessage(), exception);
		ErrorResponse errorResponse = new ErrorResponse(List.of(exception.getMessage()), BAD_REQUEST.value());
		return ResponseEntity
			.status(BAD_REQUEST.value())
			.body(errorResponse);
	}
}
