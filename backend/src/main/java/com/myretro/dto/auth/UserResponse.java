package com.myretro.dto.auth;

public record UserResponse(
        Long id,
        String email,
        String username) {
}
