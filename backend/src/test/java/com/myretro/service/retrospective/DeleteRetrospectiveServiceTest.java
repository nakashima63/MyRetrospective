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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteRetrospectiveServiceTest {

    @Mock
    private RetrospectiveRepository retrospectiveRepository;

    @InjectMocks
    private DeleteRetrospectiveService deleteRetrospectiveService;

    @Test
    void 振り返りを削除できる() {
        User user = new User("test@example.com", "hashed", "testuser");
        Retrospective retrospective = new Retrospective(user, "Sprint 1", "desc");
        given(retrospectiveRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(retrospective));

        deleteRetrospectiveService.delete(1L, 1L);

        verify(retrospectiveRepository).delete(retrospective);
    }

    @Test
    void 存在しない振り返りを削除するとRetrospectiveNotFoundException() {
        given(retrospectiveRepository.findByIdAndUserId(999L, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> deleteRetrospectiveService.delete(999L, 1L))
                .isInstanceOf(RetrospectiveNotFoundException.class);
    }
}
