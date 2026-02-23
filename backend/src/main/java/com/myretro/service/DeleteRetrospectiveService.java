package com.myretro.service;

import com.myretro.entity.Retrospective;
import com.myretro.exception.RetrospectiveNotFoundException;
import com.myretro.repository.RetrospectiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 振り返りセッションを削除するユースケース。
 * カスケード設定により、関連する KPT アイテム・アクションアイテムも同時に削除される。
 */
@Service
@RequiredArgsConstructor
public class DeleteRetrospectiveService {

    private final RetrospectiveRepository retrospectiveRepository;

    /**
     * 振り返りを削除する。
     *
     * @param id     振り返り ID
     * @param userId ユーザー ID（認可チェック用）
     * @throws RetrospectiveNotFoundException 振り返りが存在しない、または他ユーザーのデータの場合
     */
    @Transactional
    public void delete(Long id, Long userId) {
        Retrospective retrospective = retrospectiveRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RetrospectiveNotFoundException(id));
        retrospectiveRepository.delete(retrospective);
    }
}
