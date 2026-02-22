package com.myretro.security;

import com.myretro.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties(
                "test-secret-key-at-least-256-bits-long-for-hmac-sha256-testing",
                900000,   // 15分
                604800000 // 7日
        );
        jwtTokenProvider = new JwtTokenProvider(properties);
    }

    @Test
    void accessTokenを生成できる() {
        String token = jwtTokenProvider.generateAccessToken(1L, "test@example.com");

        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    void 生成したトークンからユーザーIDを取得できる() {
        String token = jwtTokenProvider.generateAccessToken(42L, "test@example.com");

        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        assertThat(userId).isEqualTo(42L);
    }

    @Test
    void 有効なトークンの検証が成功する() {
        String token = jwtTokenProvider.generateAccessToken(1L, "test@example.com");

        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }

    @Test
    void 期限切れトークンの検証が失敗する() {
        JwtProperties expiredProperties = new JwtProperties(
                "test-secret-key-at-least-256-bits-long-for-hmac-sha256-testing",
                -1000, // 既に期限切れ
                604800000
        );
        JwtTokenProvider expiredProvider = new JwtTokenProvider(expiredProperties);
        String token = expiredProvider.generateAccessToken(1L, "test@example.com");

        assertThat(jwtTokenProvider.validateToken(token)).isFalse();
    }

    @Test
    void 改ざんされたトークンの検証が失敗する() {
        String token = jwtTokenProvider.generateAccessToken(1L, "test@example.com");
        String tamperedToken = token + "tampered";

        assertThat(jwtTokenProvider.validateToken(tamperedToken)).isFalse();
    }

    @Test
    void 不正な形式のトークンの検証が失敗する() {
        assertThat(jwtTokenProvider.validateToken("not-a-jwt-token")).isFalse();
        assertThat(jwtTokenProvider.validateToken("")).isFalse();
        assertThat(jwtTokenProvider.validateToken(null)).isFalse();
    }
}
