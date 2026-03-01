package com.myretro.controller.retrospective;

import com.myretro.entity.Retrospective;
import com.myretro.entity.User;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetRetrospectiveControllerTest extends BaseRetrospectiveControllerTest {

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
}
