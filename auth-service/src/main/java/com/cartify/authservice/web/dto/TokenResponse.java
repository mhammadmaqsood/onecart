package com.cartify.authservice.web.dto;

public class TokenResponse {
    private String accessToken;
    private String refreshToken;

    public TokenResponse(String accessToken, String refreshToken) {this.accessToken = accessToken;this.refreshToken = refreshToken;}

    public String getAccessToken() {return accessToken;}
    public String getRefreshToken() {return refreshToken;}
}
