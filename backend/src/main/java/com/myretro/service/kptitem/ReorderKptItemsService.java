package com.myretro.service.kptitem;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.myretro.dto.kptitem.ReorderItem;
import com.myretro.entity.KptItem;
import com.myretro.exception.RetrospectiveNotFoundException;
import com.myretro.repository.KptItemRepository;
import com.myretro.repository.RetrospectiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * KPT アイテムの並び順を一括変更するユースケース。
 */
@Service
@RequiredArgsConstructor
public class ReorderKptItemsService {

    private final KptItemRepository kptItemRepository;
    private final RetrospectiveRepository retrospectiveRepository;

    /**
     * 指定された KPT アイテムの並び順を一括更新する。
     *
     * @param retrospectiveId 振り返り ID（認可チェック用）
     * @param userId          ユーザー ID（認可チェック用）
     * @param items           並び順を変更するアイテムのリスト（ID と新しい sortOrder のペア）
     * @throws RetrospectiveNotFoundException 振り返りが存在しない、または他ユーザーのデータの場合
     */
    @Transactional
    public void reorder(Long retrospectiveId, Long userId, List<ReorderItem> items) {
        retrospectiveRepository.findByIdAndUserId(retrospectiveId, userId)
                .orElseThrow(() -> new RetrospectiveNotFoundException(retrospectiveId));

        Map<Long, Integer> sortOrderMap = items.stream()
                .collect(Collectors.toMap(ReorderItem::id, ReorderItem::sortOrder));

        List<Long> ids = items.stream().map(ReorderItem::id).toList();
        List<KptItem> kptItems = kptItemRepository.findAllById(ids);

        for (KptItem kptItem : kptItems) {
            Integer newSortOrder = sortOrderMap.get(kptItem.getId());
            if (newSortOrder != null) {
                kptItem.update(kptItem.getType(), kptItem.getContent(), newSortOrder);
            }
        }
    }
}
