package com.myretro.controller.retrospective;

import com.myretro.entity.Retrospective;
import com.myretro.entity.User;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DeleteRetrospectiveControllerTest extends BaseRetrospectiveControllerTest {

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
