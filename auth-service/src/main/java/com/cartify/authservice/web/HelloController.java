package com.cartify.authservice.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Value("${greeting:default-greeting}")
    private String greeting;

    @GetMapping("/health/hello")
    public String hello() {
        return greeting;
    }
}