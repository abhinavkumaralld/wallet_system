package com.abhi.auth.controller;


import com.abhi.auth.common.ApiResponse;
import com.abhi.auth.dto.request.LoginRequest;
import com.abhi.auth.dto.request.RegisterRequest;
import com.abhi.auth.dto.response.AuthResponse;
import com.abhi.auth.entity.Token;
import com.abhi.auth.service.AuthService;
import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
//@CrossOrigin("*")
public class AuthController {
    @Autowired
    private AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Token>> login(@RequestBody LoginRequest loginRequest){
        System.out.println("cont log reg {}"+loginRequest.toString());
        return ResponseEntity.ok()
                .body(ApiResponse.success(authService.login(loginRequest),"success"));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<ApiResponse<Token>> refreshToken(@RequestBody Token token){
        return ResponseEntity.ok()
                .body(ApiResponse.success(authService.refrehToken(token),"success"));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Token>> signup(@RequestBody RegisterRequest registerRequest){
        System.out.println("contr reg {}"+registerRequest.toString());

        return ResponseEntity.ok()
                .body(ApiResponse.success(authService.signup(registerRequest),"success"));
    }

    @Autowired
    Tracer  tracer;

    @GetMapping("/test-trace")
    public String testTrace(){
        return "current trace "+ tracer.currentSpan();
    }
}
