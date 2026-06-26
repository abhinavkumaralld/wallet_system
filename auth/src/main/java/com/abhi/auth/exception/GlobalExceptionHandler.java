package com.abhi.auth.exception;


import com.abhi.auth.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<?>> badRequest(BadRequestException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(404,ex.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<?>> conflict(ConflictException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(205,ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> resourceNotFound(ResourceNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(202, ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<?>> unauthorized(UnauthorizedException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(403,ex.getMessage()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> exception(Exception ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(403,ex.getMessage()));
    }

}
