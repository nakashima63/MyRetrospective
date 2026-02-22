package com.myretro.controller;

import com.myretro.dto.AuthResponse;
import com.myretro.dto.LoginRequest;
import com.myretro.dto.RefreshRequest;
import com.myretro.dto.SignupRequest;
import com.myretro.dto.UserResponse;
import com.myretro.entity.User;
import com.myretro.service.LoginService;
import com.myretro.service.RefreshTokenService;
import com.myretro.service.SignupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SignupService signupService;
    private final LoginService loginService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse signup(@Valid @RequestBody SignupRequest request) {
        User user = signupService.signup(request.email(), request.password(), request.username());
        return new UserResponse(user.getId(), user.getEmail(), user.getUsername());
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return loginService.login(request.email(), request.password());
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return refreshTokenService.refresh(request.refreshToken());
    }
}
