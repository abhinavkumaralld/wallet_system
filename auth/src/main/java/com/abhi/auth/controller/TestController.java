package com.abhi.auth.controller;

import com.abhi.auth.common.ApiResponse;
import com.abhi.auth.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/api/test/health")
@CrossOrigin("*")
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> checkHealth(){
        return  ResponseEntity.ok().body(ApiResponse.success(testService.health(),"success"));
    }
}
