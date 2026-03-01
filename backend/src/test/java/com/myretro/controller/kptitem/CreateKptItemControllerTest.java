package com.myretro.controller.kptitem;

import com.myretro.dto.kptitem.CreateKptItemRequest;
import com.myretro.entity.KptType;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreateKptItemControllerTest extends BaseKptItemControllerTest {

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
}
