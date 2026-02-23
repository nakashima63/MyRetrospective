package com.myretro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myretro.dto.CreateKptItemRequest;
import com.myretro.dto.ReorderItem;
import com.myretro.dto.ReorderRequest;
import com.myretro.dto.UpdateKptItemRequest;
import com.myretro.entity.KptItem;
import com.myretro.entity.KptType;
import com.myretro.entity.Retrospective;
import com.myretro.entity.User;
import com.myretro.repository.KptItemRepository;
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

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class KptItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RetrospectiveRepository retrospectiveRepository;

    @Autowired
    private KptItemRepository kptItemRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String accessToken;
    private Retrospective retrospective;

    @BeforeEach
    void setUp() throws Exception {
        kptItemRepository.deleteAll();
        retrospectiveRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        User user = userRepository.save(
                new User("test@example.com", passwordEncoder.encode("password123"), "testuser"));

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new com.myretro.dto.LoginRequest("test@example.com", "password123"))))
                .andReturn().getResponse().getContentAsString();

        accessToken = objectMapper.readTree(loginResponse).get("accessToken").asText();
        retrospective = retrospectiveRepository.save(new Retrospective(user, "Sprint 1", "desc"));
    }

    // --- POST /api/retrospectives/:id/items ---

    @Test
    void KPTアイテム作成で201が返る() throws Exception {
        CreateKptItemRequest request = new CreateKptItemRequest(KptType.KEEP, "Good teamwork", 0);

        mockMvc.perform(post("/api/retrospectives/" + retrospective.getId() + "/items")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.type").value("KEEP"))
                .andExpect(jsonPath("$.content").value("Good teamwork"));
    }

    @Test
    void KPTアイテム作成でバリデーションエラーの場合400が返る() throws Exception {
        CreateKptItemRequest request = new CreateKptItemRequest(null, "", null);

        mockMvc.perform(post("/api/retrospectives/" + retrospective.getId() + "/items")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 存在しない振り返りにアイテム作成で404が返る() throws Exception {
        CreateKptItemRequest request = new CreateKptItemRequest(KptType.KEEP, "content", 0);

        mockMvc.perform(post("/api/retrospectives/999/items")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // --- PUT /api/retrospectives/:id/items/:itemId ---

    @Test
    void KPTアイテム更新で200が返る() throws Exception {
        KptItem saved = kptItemRepository.save(
                new KptItem(retrospective, KptType.KEEP, "Old content", 0));

        UpdateKptItemRequest request = new UpdateKptItemRequest(KptType.PROBLEM, "New content", 1);

        mockMvc.perform(put("/api/retrospectives/" + retrospective.getId() + "/items/" + saved.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("PROBLEM"))
                .andExpect(jsonPath("$.content").value("New content"))
                .andExpect(jsonPath("$.sortOrder").value(1));
    }

    @Test
    void 存在しないアイテム更新で404が返る() throws Exception {
        UpdateKptItemRequest request = new UpdateKptItemRequest(KptType.KEEP, "content", 0);

        mockMvc.perform(put("/api/retrospectives/" + retrospective.getId() + "/items/999")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // --- DELETE /api/retrospectives/:id/items/:itemId ---

    @Test
    void KPTアイテム削除で204が返る() throws Exception {
        KptItem saved = kptItemRepository.save(
                new KptItem(retrospective, KptType.KEEP, "content", 0));

        mockMvc.perform(delete("/api/retrospectives/" + retrospective.getId() + "/items/" + saved.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void 存在しないアイテム削除で404が返る() throws Exception {
        mockMvc.perform(delete("/api/retrospectives/" + retrospective.getId() + "/items/999")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    // --- PATCH /api/retrospectives/:id/items/reorder ---

    @Test
    void 並び順変更で200が返る() throws Exception {
        KptItem item1 = kptItemRepository.save(
                new KptItem(retrospective, KptType.KEEP, "Item 1", 0));
        KptItem item2 = kptItemRepository.save(
                new KptItem(retrospective, KptType.PROBLEM, "Item 2", 1));

        ReorderRequest request = new ReorderRequest(List.of(
                new ReorderItem(item1.getId(), 1),
                new ReorderItem(item2.getId(), 0)));

        mockMvc.perform(patch("/api/retrospectives/" + retrospective.getId() + "/items/reorder")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void 存在しない振り返りの並び替えで404が返る() throws Exception {
        ReorderRequest request = new ReorderRequest(List.of(new ReorderItem(1L, 0)));

        mockMvc.perform(patch("/api/retrospectives/999/items/reorder")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
