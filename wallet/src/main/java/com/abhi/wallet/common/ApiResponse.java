package com.abhi.wallet.common;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public  class ApiResponse <T>{

    private  boolean success;
    private Integer status;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public static<T> ApiResponse<T> success(T data, String message){
        return new ApiResponse<>(true,200,message,data,LocalDateTime.now());
    }
    public  static <T> ApiResponse<T> created(String message){
        return new ApiResponse<>(true,202,message,null,LocalDateTime.now());
    }
    public  static <T> ApiResponse<T> error(Integer status,String message){
        return new ApiResponse<>(false,status,message,null,LocalDateTime.now());
    }

}
