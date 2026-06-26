package com.abhi.notification.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    public boolean success;
    public Integer status;
    public String message;
    public T data;
    public LocalDateTime timestamp;


    public static <T> ApiResponse<T> success(T data,String message){
        return new ApiResponse<>(true,200,message,data,LocalDateTime.now());
    }

    public static <T> ApiResponse<T> create(String message){
        return new ApiResponse<>(true,201,message,null,LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(Integer status,String message){
        return new ApiResponse<>(true,status,message,null,LocalDateTime.now());
    }
}
