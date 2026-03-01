package com.myretro.dto.auth;

public record AuthResponse(
        String accessToken,
        String refreshToken) {
}
