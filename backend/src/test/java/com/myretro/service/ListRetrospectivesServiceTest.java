package com.myretro.service;

import com.myretro.entity.Retrospective;
import com.myretro.entity.User;
import com.myretro.repository.RetrospectiveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ListRetrospectivesServiceTest {

    @Mock
    private RetrospectiveRepository retrospectiveRepository;

    @InjectMocks
    private ListRetrospectivesService listRetrospectivesService;

    @Test
    void ユーザーの振り返り一覧を取得できる() {
        User user = new User("test@example.com", "hashed", "testuser");
        List<Retrospective> retrospectives = List.of(
                new Retrospective(user, "Sprint 2", null),
                new Retrospective(user, "Sprint 1", "First sprint"));
        given(retrospectiveRepository.findByUserIdOrderByCreatedAtDesc(1L)).willReturn(retrospectives);

        List<Retrospective> result = listRetrospectivesService.list(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Sprint 2");
    }

    @Test
    void 振り返りがない場合は空リストを返す() {
        given(retrospectiveRepository.findByUserIdOrderByCreatedAtDesc(1L)).willReturn(List.of());

        List<Retrospective> result = listRetrospectivesService.list(1L);

        assertThat(result).isEmpty();
    }
}
