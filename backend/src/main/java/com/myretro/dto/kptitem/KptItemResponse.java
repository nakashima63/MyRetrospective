package com.myretro.dto.kptitem;

import java.time.LocalDateTime;

import com.myretro.entity.KptItem;
import com.myretro.entity.KptType;

/**
 * KPT アイテムレスポンス DTO。
 *
 * @param id        KPT アイテム ID
 * @param type      KPT 種別（KEEP / PROBLEM / TRY）
 * @param content   内容
 * @param sortOrder 並び順
 * @param createdAt 作成日時
 * @param updatedAt 更新日時
 */
public record KptItemResponse(
        Long id,
        KptType type,
        String content,
        Integer sortOrder,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    /**
     * エンティティからレスポンス DTO を生成する。
     *
     * @param kptItem 変換元の KPT アイテムエンティティ
     * @return レスポンス DTO
     */
    public static KptItemResponse from(KptItem kptItem) {
        return new KptItemResponse(
                kptItem.getId(),
                kptItem.getType(),
                kptItem.getContent(),
                kptItem.getSortOrder(),
                kptItem.getCreatedAt(),
                kptItem.getUpdatedAt());
    }
}
