package com.myretro.controller.kptitem;

import com.myretro.dto.kptitem.ReorderItem;
import com.myretro.dto.kptitem.ReorderRequest;
import com.myretro.entity.KptItem;
import com.myretro.entity.KptType;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReorderKptItemsControllerTest extends BaseKptItemControllerTest {

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
