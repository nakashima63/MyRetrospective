package com.myretro.service.retrospective;

import com.myretro.entity.Retrospective;
import com.myretro.entity.User;
import com.myretro.repository.RetrospectiveRepository;
import com.myretro.repository.UserRepository;
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
class CreateRetrospectiveServiceTest {

    @Mock
    private RetrospectiveRepository retrospectiveRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreateRetrospectiveService createRetrospectiveService;

    @Test
    void 正常に振り返りを作成できる() {
        User user = new User("test@example.com", "hashed", "testuser");
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(retrospectiveRepository.save(any(Retrospective.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        Retrospective result = createRetrospectiveService.create(1L, "Sprint 1", "First sprint retro");

        assertThat(result.getTitle()).isEqualTo("Sprint 1");
        assertThat(result.getDescription()).isEqualTo("First sprint retro");
        assertThat(result.getUser()).isEqualTo(user);
        verify(retrospectiveRepository).save(any(Retrospective.class));
    }

    @Test
    void 存在しないユーザーIDで作成するとIllegalArgumentException() {
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> createRetrospectiveService.create(999L, "Sprint 1", "desc"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
