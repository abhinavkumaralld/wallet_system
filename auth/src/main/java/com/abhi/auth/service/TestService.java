package com.abhi.auth.service;

import org.springframework.stereotype.Service;

@Service
public class TestService {

    public String health(){
        return "active";
    }
}
