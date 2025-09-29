package com.cartify.authservice.controller;

import com.cartify.authservice.dao.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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
        log.info("==========================DB Hit============================");
        return "db:ok";
    }

    @GetMapping("/health/redis")
    public String redis() {
        String key = "health:ping";
        redis.opsForValue().set(key, "pong");
        return redis.opsForValue().get(key);
    }
}
