package com.myretro.service.auth;

import com.myretro.dto.auth.AuthResponse;
import com.myretro.entity.RefreshToken;
import com.myretro.entity.User;
import com.myretro.exception.InvalidTokenException;
import com.myretro.repository.RefreshTokenRepository;
import com.myretro.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public AuthResponse refresh(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(InvalidTokenException::new);

        if (refreshToken.isExpired()) {
            throw new InvalidTokenException();
        }

        User user = refreshToken.getUser();
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());

        return new AuthResponse(accessToken, refreshTokenValue);
    }
}
