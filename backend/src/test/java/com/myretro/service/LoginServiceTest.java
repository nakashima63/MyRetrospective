package com.myretro.service;

import java.util.Optional;

import com.myretro.config.JwtProperties;
import com.myretro.dto.AuthResponse;
import com.myretro.entity.RefreshToken;
import com.myretro.entity.User;
import com.myretro.exception.AuthenticationFailedException;
import com.myretro.repository.RefreshTokenRepository;
import com.myretro.repository.UserRepository;
import com.myretro.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private LoginService loginService;

    @Test
    void 正しい認証情報でAuthResponseが返る() {
        User user = createTestUser();
        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("password123", "hashed-password")).willReturn(true);
        given(jwtTokenProvider.generateAccessToken(1L, "test@example.com")).willReturn("access-token");
        given(jwtProperties.refreshTokenExpiration()).willReturn(604800000L);
        given(refreshTokenRepository.save(any(RefreshToken.class))).willAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = loginService.login("test@example.com", "password123");

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isNotNull().isNotEmpty();
    }

    @Test
    void 存在しないメールでAuthenticationFailedException() {
        given(userRepository.findByEmail("unknown@example.com")).willReturn(Optional.empty());

        assertThatThrownBy(() -> loginService.login("unknown@example.com", "password123"))
                .isInstanceOf(AuthenticationFailedException.class);
    }

    @Test
    void パスワード不一致でAuthenticationFailedException() {
        User user = createTestUser();
        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrong-password", "hashed-password")).willReturn(false);

        assertThatThrownBy(() -> loginService.login("test@example.com", "wrong-password"))
                .isInstanceOf(AuthenticationFailedException.class);
    }

    @Test
    void ログイン時にRefreshTokenがDBに保存される() {
        User user = createTestUser();
        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("password123", "hashed-password")).willReturn(true);
        given(jwtTokenProvider.generateAccessToken(1L, "test@example.com")).willReturn("access-token");
        given(jwtProperties.refreshTokenExpiration()).willReturn(604800000L);
        given(refreshTokenRepository.save(any(RefreshToken.class))).willAnswer(invocation -> invocation.getArgument(0));

        loginService.login("test@example.com", "password123");

        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    private User createTestUser() {
        User user = new User("test@example.com", "hashed-password", "testuser");
        // リフレクションで id をセット
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
