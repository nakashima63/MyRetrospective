package com.myretro.service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.myretro.dto.AuthResponse;
import com.myretro.entity.RefreshToken;
import com.myretro.entity.User;
import com.myretro.exception.InvalidTokenException;
import com.myretro.repository.RefreshTokenRepository;
import com.myretro.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void 有効なrefreshTokenで新しいaccessTokenが発行される() {
        User user = createTestUser();
        RefreshToken refreshToken = new RefreshToken(user, "valid-token", LocalDateTime.now().plusDays(7));
        given(refreshTokenRepository.findByToken("valid-token")).willReturn(Optional.of(refreshToken));
        given(jwtTokenProvider.generateAccessToken(1L, "test@example.com")).willReturn("new-access-token");

        AuthResponse response = refreshTokenService.refresh("valid-token");

        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.refreshToken()).isEqualTo("valid-token");
    }

    @Test
    void 存在しないrefreshTokenでInvalidTokenException() {
        given(refreshTokenRepository.findByToken("unknown-token")).willReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.refresh("unknown-token"))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void 期限切れrefreshTokenでInvalidTokenException() {
        User user = createTestUser();
        RefreshToken expiredToken = new RefreshToken(user, "expired-token", LocalDateTime.now().minusSeconds(1));
        given(refreshTokenRepository.findByToken("expired-token")).willReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> refreshTokenService.refresh("expired-token"))
                .isInstanceOf(InvalidTokenException.class);
    }

    private User createTestUser() {
        User user = new User("test@example.com", "hashed-password", "testuser");
        try {
            var idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return user;
    }
}
