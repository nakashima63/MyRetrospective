package com.myretro.service.retrospective;

import com.myretro.entity.Retrospective;
import com.myretro.exception.RetrospectiveNotFoundException;
import com.myretro.repository.RetrospectiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 振り返りセッションを更新するユースケース。
 */
@Service
@RequiredArgsConstructor
public class UpdateRetrospectiveService {

    private final RetrospectiveRepository retrospectiveRepository;

    /**
     * 振り返りのタイトルと説明を更新する。
     *
     * @param id          振り返り ID
     * @param userId      ユーザー ID（認可チェック用）
     * @param title       新しいタイトル
     * @param description 新しい説明
     * @return 更新された振り返りエンティティ
     * @throws RetrospectiveNotFoundException 振り返りが存在しない、または他ユーザーのデータの場合
     */
    @Transactional
    public Retrospective update(Long id, Long userId, String title, String description) {
        Retrospective retrospective = retrospectiveRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RetrospectiveNotFoundException(id));
        retrospective.update(title, description);
        return retrospective;
    }
}
