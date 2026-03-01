package com.myretro.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myretro.dto.auth.LoginRequest;
import com.myretro.dto.auth.RefreshRequest;
import com.myretro.dto.auth.SignupRequest;
import com.myretro.entity.User;
import com.myretro.repository.RefreshTokenRepository;
import com.myretro.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void サインアップ成功で201が返る() throws Exception {
        SignupRequest request = new SignupRequest("test@example.com", "password123", "testuser");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void サインアップでバリデーションエラーの場合400が返る() throws Exception {
        SignupRequest request = new SignupRequest("invalid-email", "short", "");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void サインアップで重複メールの場合409が返る() throws Exception {
        userRepository.save(new User("existing@example.com", passwordEncoder.encode("password"), "existing"));

        SignupRequest request = new SignupRequest("existing@example.com", "password123", "testuser");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void ログイン成功で200とトークンが返る() throws Exception {
        userRepository.save(new User("test@example.com", passwordEncoder.encode("password123"), "testuser"));

        LoginRequest request = new LoginRequest("test@example.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andExpect(jsonPath("$.refreshToken", notNullValue()));
    }

    @Test
    void ログイン失敗で401が返る() throws Exception {
        LoginRequest request = new LoginRequest("nonexistent@example.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void リフレッシュ成功で200が返る() throws Exception {
        // まずサインアップしてログイン
        userRepository.save(new User("test@example.com", passwordEncoder.encode("password123"), "testuser"));

        LoginRequest loginRequest = new LoginRequest("test@example.com", "password123");
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn().getResponse().getContentAsString();

        String refreshToken = objectMapper.readTree(loginResponse).get("refreshToken").asText();

        RefreshRequest refreshRequest = new RefreshRequest(refreshToken);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", notNullValue()));
    }

    @Test
    void リフレッシュで無効トークンの場合401が返る() throws Exception {
        RefreshRequest request = new RefreshRequest("invalid-token");

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 認証なしで保護エンドポイントアクセスで401が返る() throws Exception {
        mockMvc.perform(get("/api/retrospectives"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 有効トークンで保護エンドポイントアクセスで401にならない() throws Exception {
        userRepository.save(new User("test@example.com", passwordEncoder.encode("password123"), "testuser"));

        LoginRequest loginRequest = new LoginRequest("test@example.com", "password123");
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn().getResponse().getContentAsString();

        String accessToken = objectMapper.readTree(loginResponse).get("accessToken").asText();

        // 保護エンドポイントに認証付きでアクセス → 401 にならないこと
        mockMvc.perform(get("/api/retrospectives")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }
}
