package com.myretro.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken) {
}
