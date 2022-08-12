package com.prgrms.tenwonmoa.exception.handler;

import static com.prgrms.tenwonmoa.exception.message.Message.*;
import static org.springframework.http.HttpStatus.*;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.prgrms.tenwonmoa.exception.AlreadyExistException;
import com.prgrms.tenwonmoa.exception.UnauthorizedUserException;
import com.prgrms.tenwonmoa.exception.message.Message;
import com.prgrms.tenwonmoa.exception.response.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	// Client의 잘못된 요청으로 인한 에러 처리
	// 400 : BAD_REQUEST - 잘못된 요청
	@ExceptionHandler({IllegalArgumentException.class, AlreadyExistException.class})
	public ResponseEntity<ErrorResponse> handleClientBadRequest(RuntimeException exception) {
		ErrorResponse errorResponse = new ErrorResponse(List.of(exception.getMessage()), BAD_REQUEST.value());
		log.info(exception.getMessage(), exception);

		return ResponseEntity
			.status(BAD_REQUEST.value())
			.body(errorResponse);
	}

	// Client의 잘못된 요청으로 인한 에러 처리
	// 400 : Wrong Date Format - 잘못된 날짜 포맷 요청 (HttpMessage를 Convert 할때 발생하는 에러)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleHttpMessageConversionException(
		HttpMessageNotReadableException exception) {
		List<Throwable> causes = getCauses(exception);

		ErrorResponse errorResponse;
		if (causes.get(causes.size() - 1) instanceof DateTimeParseException) {
			errorResponse = new ErrorResponse(List.of(WRONG_DATE_TIME_FORMAT.getMessage()), BAD_REQUEST.value());
		} else {
			errorResponse = new ErrorResponse(List.of(exception.getMessage()), BAD_REQUEST.value());
		}
		log.info(errorResponse.getMessages().get(0), exception);

		return ResponseEntity
			.status(BAD_REQUEST.value())
			.body(errorResponse);
	}

	// @Valid error handling
	@ExceptionHandler(BindException.class)
	public ResponseEntity<ErrorResponse> handleInstanceBindException(BindException exception) {
		BindingResult bindingResult = exception.getBindingResult();
		List<String> errors = bindingResult.getAllErrors()
			.stream()
			.map(DefaultMessageSourceResolvable::getDefaultMessage)
			.collect(Collectors.toList());

		ErrorResponse errorResponse = new ErrorResponse(errors, BAD_REQUEST.value());
		log.info(errorResponse.getMessages().get(0), exception);

		return ResponseEntity
			.status(BAD_REQUEST)
			.body(errorResponse);
	}

	// @Validated error handling
	@ExceptionHandler({ConstraintViolationException.class, MethodArgumentTypeMismatchException.class})
	public ResponseEntity<ErrorResponse> handleParameterBindException(
		ConstraintViolationException exception) {
		log.info(exception.getMessage(), exception);
		ErrorResponse errorResponse = new ErrorResponse(List.of(exception.getMessage()), BAD_REQUEST.value());

		return ResponseEntity
			.status(BAD_REQUEST)
			.body(errorResponse);
	}

	// 401: 잘못된 토큰으로 요청
	@ExceptionHandler({JWTVerificationException.class})
	public ResponseEntity<ErrorResponse> handleUnauthenticated(JWTVerificationException exception) {
		log.error(exception.getMessage(), exception);
		ErrorResponse errorResponse = new ErrorResponse(List.of(exception.getMessage()), UNAUTHORIZED.value());
		return ResponseEntity
			.status(UNAUTHORIZED.value())
			.body(errorResponse);
	}

	// 403 : 권한 없음
	@ExceptionHandler({UnauthorizedUserException.class})
	public ResponseEntity<ErrorResponse> handleUnAuthorized(Exception exception) {
		log.error(exception.getMessage(), exception);
		ErrorResponse errorResponse = new ErrorResponse(List.of(exception.getMessage()), FORBIDDEN.value());
		return ResponseEntity
			.status(FORBIDDEN.value())
			.body(errorResponse);
	}

	// 404: NotFound - 해당 값 없음
	@ExceptionHandler({NoSuchElementException.class})
	public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException exception) {
		log.info(exception.getMessage(), exception);
		ErrorResponse errorResponse = new ErrorResponse(List.of(exception.getMessage()), NOT_FOUND.value());
		return ResponseEntity
			.status(NOT_FOUND.value())
			.body(errorResponse);
	}

	// 405 : Method Not Allowed
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
		HttpRequestMethodNotSupportedException exception) {
		log.debug(exception.getMessage(), exception);
		ErrorResponse errorResponse = new ErrorResponse(List.of(exception.getMessage()), METHOD_NOT_ALLOWED.value());
		return ResponseEntity
			.status(METHOD_NOT_ALLOWED.value())
			.body(errorResponse);
	}

	// 500: 버그
	@ExceptionHandler({RuntimeException.class})
	public ResponseEntity<ErrorResponse> handleBug(RuntimeException exception) {
		log.error(exception.getMessage(), exception);
		ErrorResponse errorResponse = new ErrorResponse(List.of(Message.INTERNAL_SERVER_ERROR.getMessage()),
			HttpStatus.INTERNAL_SERVER_ERROR.value());
		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
			.body(errorResponse);
	}

	private List<Throwable> getCauses(Throwable exception) {
		List<Throwable> causes = new ArrayList<>();
		Throwable curException = exception;
		while (true) {
			Throwable cause = curException.getCause();
			if (cause == null) {
				break;
			}
			causes.add(cause);
			curException = cause;
		}
		return causes;
	}
}
