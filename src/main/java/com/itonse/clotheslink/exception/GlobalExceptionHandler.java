package com.itonse.clotheslink.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException .class)
    public ResponseEntity<List<String>> handleValidException(MethodArgumentNotValidException exception) {
        List<String> errors = new ArrayList<>();
        exception.getBindingResult().getAllErrors()
                .forEach((error -> errors.add(error.getDefaultMessage())));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException (CustomException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(exception.getErrorCode().getHttpStatus())
                .errorCode(exception.getErrorCode())
                .message(exception.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse
                , errorResponse.getErrorCode().getHttpStatus());
    }
}