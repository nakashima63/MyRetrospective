package com.myretro.service;

import com.myretro.dto.ReorderItem;
import com.myretro.entity.KptItem;
import com.myretro.entity.KptType;
import com.myretro.entity.Retrospective;
import com.myretro.entity.User;
import com.myretro.exception.RetrospectiveNotFoundException;
import com.myretro.repository.KptItemRepository;
import com.myretro.repository.RetrospectiveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ReorderKptItemsServiceTest {

    @Mock
    private KptItemRepository kptItemRepository;

    @Mock
    private RetrospectiveRepository retrospectiveRepository;

    @InjectMocks
    private ReorderKptItemsService reorderKptItemsService;

    @Test
    void KPTアイテムの並び順を更新できる() {
        User user = new User("test@example.com", "hashed", "testuser");
        Retrospective retrospective = new Retrospective(user, "Sprint 1", "desc");
        KptItem item1 = new KptItem(retrospective, KptType.KEEP, "Item 1", 0);
        KptItem item2 = new KptItem(retrospective, KptType.PROBLEM, "Item 2", 1);
        ReflectionTestUtils.setField(item1, "id", 10L);
        ReflectionTestUtils.setField(item2, "id", 20L);

        given(retrospectiveRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(retrospective));
        given(kptItemRepository.findAllById(List.of(10L, 20L))).willReturn(List.of(item1, item2));

        List<ReorderItem> reorderItems = List.of(
                new ReorderItem(10L, 1),
                new ReorderItem(20L, 0));

        reorderKptItemsService.reorder(1L, 1L, reorderItems);

        assertThat(item1.getSortOrder()).isEqualTo(1);
        assertThat(item2.getSortOrder()).isEqualTo(0);
    }

    @Test
    void 存在しない振り返りの並び替えでRetrospectiveNotFoundException() {
        given(retrospectiveRepository.findByIdAndUserId(999L, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reorderKptItemsService.reorder(999L, 1L, List.of()))
                .isInstanceOf(RetrospectiveNotFoundException.class);
    }
}
