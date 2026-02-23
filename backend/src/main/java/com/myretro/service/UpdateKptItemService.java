package com.myretro.service;

import com.myretro.entity.KptItem;
import com.myretro.entity.KptType;
import com.myretro.exception.KptItemNotFoundException;
import com.myretro.exception.RetrospectiveNotFoundException;
import com.myretro.repository.KptItemRepository;
import com.myretro.repository.RetrospectiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * KPT アイテムを更新するユースケース。
 */
@Service
@RequiredArgsConstructor
public class UpdateKptItemService {

    private final KptItemRepository kptItemRepository;
    private final RetrospectiveRepository retrospectiveRepository;

    /**
     * KPT アイテムの種別・内容・並び順を更新する。
     *
     * @param retrospectiveId 振り返り ID（認可チェック用）
     * @param itemId          更新対象の KPT アイテム ID
     * @param userId          ユーザー ID（認可チェック用）
     * @param type            新しい KPT 種別
     * @param content         新しい内容
     * @param sortOrder       新しい並び順
     * @return 更新された KPT アイテム
     * @throws RetrospectiveNotFoundException 振り返りが存在しない、または他ユーザーのデータの場合
     * @throws KptItemNotFoundException       KPT アイテムが存在しない場合
     */
    @Transactional
    public KptItem update(Long retrospectiveId, Long itemId, Long userId,
                          KptType type, String content, Integer sortOrder) {
        retrospectiveRepository.findByIdAndUserId(retrospectiveId, userId)
                .orElseThrow(() -> new RetrospectiveNotFoundException(retrospectiveId));
        KptItem kptItem = kptItemRepository.findById(itemId)
                .orElseThrow(() -> new KptItemNotFoundException(itemId));
        kptItem.update(type, content, sortOrder);
        return kptItem;
    }
}
