package com.myretro.service.retrospective;

import java.util.List;

import com.myretro.entity.Retrospective;
import com.myretro.repository.RetrospectiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ログインユーザーの振り返り一覧を取得するユースケース。
 */
@Service
@RequiredArgsConstructor
public class ListRetrospectivesService {

    private final RetrospectiveRepository retrospectiveRepository;

    /**
     * 指定ユーザーの振り返り一覧を作成日時の降順で取得する。
     *
     * @param userId ユーザー ID
     * @return 振り返りのリスト（存在しない場合は空リスト）
     */
    @Transactional(readOnly = true)
    public List<Retrospective> list(Long userId) {
        return retrospectiveRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
