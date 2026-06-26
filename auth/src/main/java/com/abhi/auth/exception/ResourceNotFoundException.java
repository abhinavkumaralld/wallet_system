package com.abhi.auth.exception;

public class ResourceNotFoundException extends  RuntimeException{
    public ResourceNotFoundException(String message){
        super(message);
    }
}
