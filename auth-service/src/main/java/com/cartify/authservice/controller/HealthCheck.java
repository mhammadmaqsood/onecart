package com.cartify.authservice.controller;

import com.cartify.authservice.dao.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthCheck {
    private final UserDao userDao;
    private final StringRedisTemplate redis;

    @GetMapping("/health/app")
    public String app() {
        return "auth-service:ok";
    }

    @GetMapping("/health/db")
    public String db() {
        userDao.findById(-1); // harmless DB call
        return "db:ok";
    }

    @GetMapping("/health/redis")
    public String redis() {
        String key = "health:ping";
        redis.opsForValue().set(key, "pong");
        return redis.opsForValue().get(key);
    }
}
