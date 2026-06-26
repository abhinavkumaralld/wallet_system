package com.abhi.notification.exception;


import com.abhi.notification.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadReqeuest.class)
    public ResponseEntity<ApiResponse<?>> badRequest(BadReqeuest badReqeuest){
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400,badReqeuest.getMessage()));

    }

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<ApiResponse<?>> resourceNotFound(ResourceNotFound resourceNotFound){
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400,resourceNotFound.getMessage()));
    }
}
