package com.myretro.controller.kptitem;

import com.myretro.entity.KptItem;
import com.myretro.entity.KptType;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DeleteKptItemControllerTest extends BaseKptItemControllerTest {

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
}
