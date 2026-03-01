package com.myretro.service.kptitem;

import com.myretro.entity.KptItem;
import com.myretro.exception.KptItemNotFoundException;
import com.myretro.exception.RetrospectiveNotFoundException;
import com.myretro.repository.KptItemRepository;
import com.myretro.repository.RetrospectiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * KPT アイテムを削除するユースケース。
 */
@Service
@RequiredArgsConstructor
public class DeleteKptItemService {

    private final KptItemRepository kptItemRepository;
    private final RetrospectiveRepository retrospectiveRepository;

    /**
     * KPT アイテムを削除する。
     *
     * @param retrospectiveId 振り返り ID（認可チェック用）
     * @param itemId          削除対象の KPT アイテム ID
     * @param userId          ユーザー ID（認可チェック用）
     * @throws RetrospectiveNotFoundException 振り返りが存在しない、または他ユーザーのデータの場合
     * @throws KptItemNotFoundException       KPT アイテムが存在しない場合
     */
    @Transactional
    public void delete(Long retrospectiveId, Long itemId, Long userId) {
        retrospectiveRepository.findByIdAndUserId(retrospectiveId, userId)
                .orElseThrow(() -> new RetrospectiveNotFoundException(retrospectiveId));
        KptItem kptItem = kptItemRepository.findById(itemId)
                .orElseThrow(() -> new KptItemNotFoundException(itemId));
        kptItemRepository.delete(kptItem);
    }
}
