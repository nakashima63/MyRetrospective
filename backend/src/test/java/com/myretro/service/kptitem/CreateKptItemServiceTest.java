package com.myretro.service.kptitem;

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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateKptItemServiceTest {

    @Mock
    private KptItemRepository kptItemRepository;

    @Mock
    private RetrospectiveRepository retrospectiveRepository;

    @InjectMocks
    private CreateKptItemService createKptItemService;

    @Test
    void KPTアイテムを作成できる() {
        User user = new User("test@example.com", "hashed", "testuser");
        Retrospective retrospective = new Retrospective(user, "Sprint 1", "desc");
        given(retrospectiveRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(retrospective));
        given(kptItemRepository.save(any(KptItem.class))).willAnswer(invocation -> invocation.getArgument(0));

        KptItem result = createKptItemService.create(1L, 1L, KptType.KEEP, "Good teamwork", 0);

        assertThat(result.getType()).isEqualTo(KptType.KEEP);
        assertThat(result.getContent()).isEqualTo("Good teamwork");
        assertThat(result.getSortOrder()).isEqualTo(0);
        verify(kptItemRepository).save(any(KptItem.class));
    }

    @Test
    void 存在しない振り返りにアイテム追加するとRetrospectiveNotFoundException() {
        given(retrospectiveRepository.findByIdAndUserId(999L, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> createKptItemService.create(999L, 1L, KptType.KEEP, "content", 0))
                .isInstanceOf(RetrospectiveNotFoundException.class);
    }
}
