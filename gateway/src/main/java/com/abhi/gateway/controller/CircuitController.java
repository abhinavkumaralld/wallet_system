package com.abhi.gateway.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CircuitController {

    @RequestMapping(value = "/fallback/wallet", method = {
            RequestMethod.GET, RequestMethod.POST,
            RequestMethod.PUT, RequestMethod.DELETE
    })
    public ResponseEntity<ApiResponse<?>> fallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(503, "Service not available as of now"));
    }
}