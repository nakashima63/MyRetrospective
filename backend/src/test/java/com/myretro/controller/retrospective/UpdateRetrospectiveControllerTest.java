package com.myretro.controller.retrospective;

import com.myretro.dto.retrospective.UpdateRetrospectiveRequest;
import com.myretro.entity.Retrospective;
import com.myretro.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UpdateRetrospectiveControllerTest extends BaseRetrospectiveControllerTest {

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
}
