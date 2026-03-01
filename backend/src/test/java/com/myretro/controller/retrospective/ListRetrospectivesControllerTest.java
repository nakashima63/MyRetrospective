package com.myretro.controller.retrospective;

import com.myretro.entity.Retrospective;
import com.myretro.entity.User;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ListRetrospectivesControllerTest extends BaseRetrospectiveControllerTest {

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
}
