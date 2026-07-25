package com.abhi.auth.controller;

import com.abhi.auth.common.ApiResponse;
import com.abhi.auth.dto.response.UserDetailsResponse;
import com.abhi.auth.service.AuthService;
import com.abhi.auth.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/api/user")
@CrossOrigin("*")
public class TestController {

    @Autowired
    private TestService testService;

    @Autowired
    private AuthService authService;

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> checkHealth(){
        return  ResponseEntity.ok().body(ApiResponse.success(testService.health(),"success"));
    }
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDetailsResponse>> getDetails(@PathVariable Long userId){
        System.out.println("user id "+userId);
        return  ResponseEntity.ok().body(ApiResponse.success(authService.getUserDetails(userId),"success"));
    }
}
