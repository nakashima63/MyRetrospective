package com.myretro.controller.retrospective;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myretro.dto.retrospective.CreateRetrospectiveRequest;
import com.myretro.dto.retrospective.UpdateRetrospectiveRequest;
import com.myretro.entity.Retrospective;
import com.myretro.entity.User;
import com.myretro.repository.RetrospectiveRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RetrospectiveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RetrospectiveRepository retrospectiveRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String accessToken;
    private String otherUserAccessToken;

    @BeforeEach
    void setUp() throws Exception {
        retrospectiveRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        // ユーザー作成してログイン → accessToken 取得
        accessToken = createUserAndLogin("test@example.com", "password123", "testuser");
        otherUserAccessToken = createUserAndLogin("other@example.com", "password123", "otheruser");
    }

    private String createUserAndLogin(String email, String password, String username) throws Exception {
        userRepository.save(new User(email, passwordEncoder.encode(password), username));

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new com.myretro.dto.auth.LoginRequest(email, password))))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(loginResponse).get("accessToken").asText();
    }

    // --- POST /api/retrospectives ---

    @Test
    void 振り返り作成で201が返る() throws Exception {
        CreateRetrospectiveRequest request = new CreateRetrospectiveRequest("Sprint 1", "First sprint retro");

        mockMvc.perform(post("/api/retrospectives")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Sprint 1"))
                .andExpect(jsonPath("$.description").value("First sprint retro"));
    }

    @Test
    void 振り返り作成でバリデーションエラーの場合400が返る() throws Exception {
        CreateRetrospectiveRequest request = new CreateRetrospectiveRequest("", null);

        mockMvc.perform(post("/api/retrospectives")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 振り返り作成で認証なしの場合401が返る() throws Exception {
        CreateRetrospectiveRequest request = new CreateRetrospectiveRequest("Sprint 1", "desc");

        mockMvc.perform(post("/api/retrospectives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // --- GET /api/retrospectives ---

    @Test
    void 振り返り一覧取得で200が返る() throws Exception {
        User user = userRepository.findByEmail("test@example.com").orElseThrow();
        retrospectiveRepository.save(new Retrospective(user, "Sprint 1", "desc1"));
        retrospectiveRepository.save(new Retrospective(user, "Sprint 2", "desc2"));

        mockMvc.perform(get("/api/retrospectives")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void 振り返りがない場合は空リストで200が返る() throws Exception {
        mockMvc.perform(get("/api/retrospectives")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // --- GET /api/retrospectives/:id ---

    @Test
    void 振り返り詳細取得で200が返る() throws Exception {
        User user = userRepository.findByEmail("test@example.com").orElseThrow();
        Retrospective saved = retrospectiveRepository.save(new Retrospective(user, "Sprint 1", "desc"));

        mockMvc.perform(get("/api/retrospectives/" + saved.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Sprint 1"))
                .andExpect(jsonPath("$.kptItems").isArray());
    }

    @Test
    void 存在しない振り返りの詳細取得で404が返る() throws Exception {
        mockMvc.perform(get("/api/retrospectives/999")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void 他ユーザーの振り返りにアクセスで404が返る() throws Exception {
        User user = userRepository.findByEmail("test@example.com").orElseThrow();
        Retrospective saved = retrospectiveRepository.save(new Retrospective(user, "Sprint 1", "desc"));

        mockMvc.perform(get("/api/retrospectives/" + saved.getId())
                        .header("Authorization", "Bearer " + otherUserAccessToken))
                .andExpect(status().isNotFound());
    }

    // --- PUT /api/retrospectives/:id ---

    @Test
    void 振り返り更新で200が返る() throws Exception {
        User user = userRepository.findByEmail("test@example.com").orElseThrow();
        Retrospective saved = retrospectiveRepository.save(new Retrospective(user, "Old Title", "Old desc"));

        UpdateRetrospectiveRequest request = new UpdateRetrospectiveRequest("New Title", "New desc");

        mockMvc.perform(put("/api/retrospectives/" + saved.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.description").value("New desc"));
    }

    @Test
    void 存在しない振り返りの更新で404が返る() throws Exception {
        UpdateRetrospectiveRequest request = new UpdateRetrospectiveRequest("Title", "desc");

        mockMvc.perform(put("/api/retrospectives/999")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // --- DELETE /api/retrospectives/:id ---

    @Test
    void 振り返り削除で204が返る() throws Exception {
        User user = userRepository.findByEmail("test@example.com").orElseThrow();
        Retrospective saved = retrospectiveRepository.save(new Retrospective(user, "Sprint 1", "desc"));

        mockMvc.perform(delete("/api/retrospectives/" + saved.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void 存在しない振り返りの削除で404が返る() throws Exception {
        mockMvc.perform(delete("/api/retrospectives/999")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }
}
