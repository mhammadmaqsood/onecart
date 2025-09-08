package com.cartify.authservice.controller;

import com.cartify.authservice.service.AuthService;
import com.cartify.authservice.web.dto.LoginRequest;
import com.cartify.authservice.web.dto.RegisterRequest;
import com.cartify.authservice.web.dto.TokenResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public long register(@RequestBody @Valid RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody @Valid LoginRequest loginRequest) throws Exception {
        return authService.login(loginRequest);
    }
}
