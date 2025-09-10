package com.cartify.authservice.service;

import com.cartify.authservice.dao.UserDao;
import com.cartify.authservice.domain.User;
import com.cartify.authservice.security.JwtService;
import com.cartify.authservice.web.dto.LoginRequest;
import com.cartify.authservice.web.dto.RegisterRequest;
import com.cartify.authservice.web.dto.TokenResponse;
import com.cartify.common.error.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokens;

    public long register(RegisterRequest registerRequest) {
        //hash
        String hash = BCrypt.hashpw(registerRequest.getPassword(), BCrypt.gensalt(12));

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .passwordHash(hash)
                .mfaSecret(null)
                .status("ACTIVE")
                .build();
        return userDao.insert(user);
    }

    public TokenResponse login(LoginRequest loginRequest) throws Exception{
        Optional<User> userOpt =
                loginRequest.getUsernameOrEmail().contains("@")
                        ? userDao.findByUsername(loginRequest.getUsernameOrEmail())
                        : userDao.findByUsername(loginRequest.getUsernameOrEmail());

        User user = userOpt.orElseThrow(() -> new BadRequestException("Invalid Credentials"));
        if(!BCrypt.checkpw(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid Credentials");
        }

        String access = jwtService.createAccessToken(user.getId(), user.getUsername(), user.getEmail(), 3600);
        String refresh = refreshTokens.issue(user.getId());

        return new TokenResponse(access, refresh);
    }
}
