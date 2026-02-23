package com.myretro.service;

import com.myretro.entity.KptItem;
import com.myretro.entity.KptType;
import com.myretro.entity.Retrospective;
import com.myretro.exception.RetrospectiveNotFoundException;
import com.myretro.repository.KptItemRepository;
import com.myretro.repository.RetrospectiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * KPT アイテムを新規作成するユースケース。
 */
@Service
@RequiredArgsConstructor
public class CreateKptItemService {

    private final KptItemRepository kptItemRepository;
    private final RetrospectiveRepository retrospectiveRepository;

    /**
     * 指定された振り返りに KPT アイテムを追加する。
     *
     * @param retrospectiveId 振り返り ID
     * @param userId          ユーザー ID（認可チェック用）
     * @param type            KPT 種別（KEEP / PROBLEM / TRY）
     * @param content         内容
     * @param sortOrder       並び順（null の場合は 0）
     * @return 作成された KPT アイテム
     * @throws RetrospectiveNotFoundException 振り返りが存在しない、または他ユーザーのデータの場合
     */
    @Transactional
    public KptItem create(Long retrospectiveId, Long userId, KptType type, String content, Integer sortOrder) {
        Retrospective retrospective = retrospectiveRepository.findByIdAndUserId(retrospectiveId, userId)
                .orElseThrow(() -> new RetrospectiveNotFoundException(retrospectiveId));
        KptItem kptItem = new KptItem(retrospective, type, content, sortOrder != null ? sortOrder : 0);
        return kptItemRepository.save(kptItem);
    }
}
