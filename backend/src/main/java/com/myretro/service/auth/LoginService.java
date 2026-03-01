package com.myretro.service.auth;

import java.time.LocalDateTime;
import java.util.UUID;

import com.myretro.config.JwtProperties;
import com.myretro.dto.auth.AuthResponse;
import com.myretro.entity.RefreshToken;
import com.myretro.entity.User;
import com.myretro.exception.AuthenticationFailedException;
import com.myretro.repository.RefreshTokenRepository;
import com.myretro.repository.UserRepository;
import com.myretro.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Transactional
    public AuthResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(AuthenticationFailedException::new);

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new AuthenticationFailedException();
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());

        String refreshTokenValue = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(jwtProperties.refreshTokenExpiration() / 1000);
        RefreshToken refreshToken = new RefreshToken(user, refreshTokenValue, expiresAt);
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(accessToken, refreshTokenValue);
    }
}
