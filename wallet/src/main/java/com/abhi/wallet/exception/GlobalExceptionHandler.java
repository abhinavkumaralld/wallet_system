package com.abhi.wallet.exception;

import com.abhi.wallet.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<?>> badRequest(BadRequestException requestException){
        return ResponseEntity.status(400).body(ApiResponse.error(400, requestException.getMessage()));
    }

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<ApiResponse<?>> resourceNotFound(ResourceNotFound resourceNotFound){
        return ResponseEntity.status(400).body(ApiResponse.error(400, resourceNotFound.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> exception(Exception ex){
        return ResponseEntity.status(400).body(ApiResponse.error(400, ex.getMessage()));
    }
}