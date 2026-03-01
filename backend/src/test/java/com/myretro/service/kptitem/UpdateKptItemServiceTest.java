package com.myretro.service.kptitem;

import com.myretro.entity.KptItem;
import com.myretro.entity.KptType;
import com.myretro.entity.Retrospective;
import com.myretro.entity.User;
import com.myretro.exception.KptItemNotFoundException;
import com.myretro.exception.RetrospectiveNotFoundException;
import com.myretro.repository.KptItemRepository;
import com.myretro.repository.RetrospectiveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UpdateKptItemServiceTest {

    @Mock
    private KptItemRepository kptItemRepository;

    @Mock
    private RetrospectiveRepository retrospectiveRepository;

    @InjectMocks
    private UpdateKptItemService updateKptItemService;

    @Test
    void KPTアイテムを更新できる() {
        User user = new User("test@example.com", "hashed", "testuser");
        Retrospective retrospective = new Retrospective(user, "Sprint 1", "desc");
        KptItem kptItem = new KptItem(retrospective, KptType.KEEP, "Old content", 0);
        given(retrospectiveRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(retrospective));
        given(kptItemRepository.findById(10L)).willReturn(Optional.of(kptItem));

        KptItem result = updateKptItemService.update(1L, 10L, 1L, KptType.PROBLEM, "New content", 1);

        assertThat(result.getType()).isEqualTo(KptType.PROBLEM);
        assertThat(result.getContent()).isEqualTo("New content");
        assertThat(result.getSortOrder()).isEqualTo(1);
    }

    @Test
    void 存在しない振り返りのアイテム更新でRetrospectiveNotFoundException() {
        given(retrospectiveRepository.findByIdAndUserId(999L, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> updateKptItemService.update(999L, 10L, 1L, KptType.KEEP, "content", 0))
                .isInstanceOf(RetrospectiveNotFoundException.class);
    }

    @Test
    void 存在しないアイテム更新でKptItemNotFoundException() {
        User user = new User("test@example.com", "hashed", "testuser");
        Retrospective retrospective = new Retrospective(user, "Sprint 1", "desc");
        given(retrospectiveRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(retrospective));
        given(kptItemRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> updateKptItemService.update(1L, 999L, 1L, KptType.KEEP, "content", 0))
                .isInstanceOf(KptItemNotFoundException.class);
    }
}
