package com.myretro.controller.retrospective;

import com.myretro.dto.retrospective.CreateRetrospectiveRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreateRetrospectiveControllerTest extends BaseRetrospectiveControllerTest {

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
}
