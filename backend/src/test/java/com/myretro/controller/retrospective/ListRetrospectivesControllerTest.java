package com.myretro.controller.retrospective;

import com.myretro.entity.KptItem;
import com.myretro.entity.KptType;
import com.myretro.entity.Retrospective;
import com.myretro.entity.User;
import com.myretro.repository.KptItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ListRetrospectivesControllerTest extends BaseRetrospectiveControllerTest {

    @Autowired
    private KptItemRepository kptItemRepository;

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

    @Test
    void 振り返り一覧にKPTカウントが含まれる() throws Exception {
        User user = userRepository.findByEmail("test@example.com").orElseThrow();
        Retrospective retro = retrospectiveRepository.save(new Retrospective(user, "Sprint 1", "desc1"));

        kptItemRepository.save(new KptItem(retro, KptType.KEEP, "keep1", 0));
        kptItemRepository.save(new KptItem(retro, KptType.KEEP, "keep2", 1));
        kptItemRepository.save(new KptItem(retro, KptType.PROBLEM, "problem1", 0));
        kptItemRepository.save(new KptItem(retro, KptType.TRY, "try1", 0));
        kptItemRepository.save(new KptItem(retro, KptType.TRY, "try2", 1));
        kptItemRepository.save(new KptItem(retro, KptType.TRY, "try3", 2));

        mockMvc.perform(get("/api/retrospectives")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].keepCount").value(2))
                .andExpect(jsonPath("$[0].problemCount").value(1))
                .andExpect(jsonPath("$[0].tryCount").value(3));
    }
}
