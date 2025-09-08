package com.cartify.authservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Autowired
    private StringRedisTemplate redis;

    //7 days default
    private static final long TTL_SECONDS = 7 * 24 * 3600;

    public String issue(Long userId) {
        String token = UUID.randomUUID().toString();
        String key = key(userId, token);
        redis.opsForValue().set(key, "1", Duration.ofSeconds(TTL_SECONDS));
        return token;
    }

    public boolean validate (Long userId, String token) {
        return Boolean.TRUE.equals(redis.hasKey(key(userId, token)));
    }

    public void revoke (Long userId, String token) {
        redis.delete(key(userId, token));
    }

    private static String key(Long userId, String token) {
        return "auth:rt:" + userId + ":" + token;
    }
}
