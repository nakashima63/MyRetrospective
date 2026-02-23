package com.myretro.service;

import com.myretro.entity.Retrospective;
import com.myretro.exception.RetrospectiveNotFoundException;
import com.myretro.repository.RetrospectiveRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 振り返りセッションの詳細を取得するユースケース。
 * KPT アイテムを含む詳細情報を返す。
 */
@Service
@RequiredArgsConstructor
public class GetRetrospectiveService {

    private final RetrospectiveRepository retrospectiveRepository;

    /**
     * 指定された振り返りの詳細を取得する。
     * KPT アイテムのコレクションを初期化して返す。
     *
     * @param id     振り返り ID
     * @param userId ユーザー ID（認可チェック用）
     * @return 振り返りエンティティ（KPT アイテム初期化済み）
     * @throws RetrospectiveNotFoundException 振り返りが存在しない、または他ユーザーのデータの場合
     */
    @Transactional(readOnly = true)
    public Retrospective get(Long id, Long userId) {
        Retrospective retrospective = retrospectiveRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RetrospectiveNotFoundException(id));
        Hibernate.initialize(retrospective.getKptItems());
        return retrospective;
    }
}
