package com.myretro.controller.kptitem;

import com.myretro.dto.kptitem.UpdateKptItemRequest;
import com.myretro.entity.KptItem;
import com.myretro.entity.KptType;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UpdateKptItemControllerTest extends BaseKptItemControllerTest {

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
}
