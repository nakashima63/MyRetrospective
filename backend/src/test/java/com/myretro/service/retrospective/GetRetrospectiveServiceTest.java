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
class GetRetrospectiveServiceTest {

    @Mock
    private RetrospectiveRepository retrospectiveRepository;

    @InjectMocks
    private GetRetrospectiveService getRetrospectiveService;

    @Test
    void 振り返りを詳細取得できる() {
        User user = new User("test@example.com", "hashed", "testuser");
        Retrospective retrospective = new Retrospective(user, "Sprint 1", "desc");
        given(retrospectiveRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(retrospective));

        Retrospective result = getRetrospectiveService.get(1L, 1L);

        assertThat(result.getTitle()).isEqualTo("Sprint 1");
        assertThat(result.getDescription()).isEqualTo("desc");
    }

    @Test
    void 存在しない振り返りIDでRetrospectiveNotFoundException() {
        given(retrospectiveRepository.findByIdAndUserId(999L, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> getRetrospectiveService.get(999L, 1L))
                .isInstanceOf(RetrospectiveNotFoundException.class);
    }

    @Test
    void 他ユーザーの振り返りにアクセスするとRetrospectiveNotFoundException() {
        given(retrospectiveRepository.findByIdAndUserId(1L, 2L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> getRetrospectiveService.get(1L, 2L))
                .isInstanceOf(RetrospectiveNotFoundException.class);
    }
}
