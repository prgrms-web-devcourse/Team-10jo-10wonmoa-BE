package com.prgrms.tenwonmoa.exception.handler;

import static org.springframework.http.HttpStatus.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.prgrms.tenwonmoa.exception.AlreadyExistException;
import com.prgrms.tenwonmoa.exception.NotFoundException;
import com.prgrms.tenwonmoa.exception.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private final Logger log = LoggerFactory.getLogger(getClass());

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
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFoundException(HttpRequestMethodNotSupportedException exception) {
		log.error(exception.getMessage(), exception);
		ErrorResponse errorResponse = new ErrorResponse(List.of(exception.getMessage()), BAD_REQUEST.value());
		return ResponseEntity
			.status(BAD_REQUEST.value())
			.body(errorResponse);
	}

	@ExceptionHandler(AlreadyExistException.class)
	public ResponseEntity<ErrorResponse> handleAlreadyExistException(HttpRequestMethodNotSupportedException exception) {
		log.error(exception.getMessage(), exception);
		ErrorResponse errorResponse = new ErrorResponse(List.of(exception.getMessage()), BAD_REQUEST.value());
		return ResponseEntity
			.status(BAD_REQUEST.value())
			.body(errorResponse);
	}
}
