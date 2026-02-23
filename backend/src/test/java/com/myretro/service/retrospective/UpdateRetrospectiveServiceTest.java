package com.myretro.service.retrospective;

import com.myretro.entity.Retrospective;
import com.myretro.entity.User;
import com.myretro.exception.RetrospectiveNotFoundException;
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
class UpdateRetrospectiveServiceTest {

    @Mock
    private RetrospectiveRepository retrospectiveRepository;

    @InjectMocks
    private UpdateRetrospectiveService updateRetrospectiveService;

    @Test
    void 振り返りを更新できる() {
        User user = new User("test@example.com", "hashed", "testuser");
        Retrospective retrospective = new Retrospective(user, "Old Title", "Old desc");
        given(retrospectiveRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(retrospective));

        Retrospective result = updateRetrospectiveService.update(1L, 1L, "New Title", "New desc");

        assertThat(result.getTitle()).isEqualTo("New Title");
        assertThat(result.getDescription()).isEqualTo("New desc");
    }

    @Test
    void 存在しない振り返りを更新するとRetrospectiveNotFoundException() {
        given(retrospectiveRepository.findByIdAndUserId(999L, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> updateRetrospectiveService.update(999L, 1L, "Title", "desc"))
                .isInstanceOf(RetrospectiveNotFoundException.class);
    }
}
