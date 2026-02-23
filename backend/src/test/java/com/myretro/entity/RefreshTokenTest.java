package com.myretro.entity;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenTest {

    @Test
    void コンストラクタで正しく初期化される() {
        User user = new User("test@example.com", "hashedPassword", "testuser");
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);

        RefreshToken refreshToken = new RefreshToken(user, "token-value", expiresAt);

        assertThat(refreshToken.getUser()).isEqualTo(user);
        assertThat(refreshToken.getToken()).isEqualTo("token-value");
        assertThat(refreshToken.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(refreshToken.getCreatedAt()).isNotNull();
    }

    @Test
    void 期限内のトークンはisExpiredがfalseを返す() {
        User user = new User("test@example.com", "hashedPassword", "testuser");
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);

        RefreshToken refreshToken = new RefreshToken(user, "token-value", expiresAt);

        assertThat(refreshToken.isExpired()).isFalse();
    }

    @Test
    void 期限切れのトークンはisExpiredがtrueを返す() {
        User user = new User("test@example.com", "hashedPassword", "testuser");
        LocalDateTime expiresAt = LocalDateTime.now().minusSeconds(1);

        RefreshToken refreshToken = new RefreshToken(user, "token-value", expiresAt);

        assertThat(refreshToken.isExpired()).isTrue();
    }
}
