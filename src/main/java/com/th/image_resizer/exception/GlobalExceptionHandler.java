package com.th.image_resizer.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        log.warn("handleException", e);
        return handleExceptionInternal(CommonErrorCode.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<Object> handleRestApiException(RestApiException e) {
        log.warn("handleRestApiException", e);
        return handleExceptionInternal(e.getErrorCode());
    }

    public ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
        return handleExceptionInternal(errorCode, errorCode.getMessage());
    }

    public ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(message == null ?
                        makeErrorResponse(errorCode)
                        : makeErrorResponse(errorCode, message, null));
    }

    public ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode, List<ErrorResponse.ValidError> errors) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(errors == null ?
                        makeErrorResponse(errorCode)
                        : makeErrorResponse(errorCode, errors));
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode) {
        return makeErrorResponse(errorCode, errorCode.getMessage(), null);
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode, String message, List<ErrorResponse.ValidError> errors) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(message)
                .validErrorList(errors)
                .build();
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode, List<ErrorResponse.ValidError> errors) {
        return makeErrorResponse(errorCode, errorCode.getMessage(), errors);
    }
}
