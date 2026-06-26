package com.abhi.notification.controller;

import com.abhi.notification.common.ApiResponse;
import com.abhi.notification.dto.TransferEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notification")
//@CrossOrigin("*")
public class Controller {

    @PostMapping("/sendEmail")
    public ResponseEntity<ApiResponse<?>> sendEmail(TransferEvent transferEvent){
        return null;
    }
}
