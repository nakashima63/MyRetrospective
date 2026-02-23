package com.myretro.dto.retrospective;

import java.time.LocalDateTime;
import java.util.List;

import com.myretro.dto.kptitem.KptItemResponse;
import com.myretro.entity.Retrospective;

/**
 * 振り返り詳細レスポンス DTO（KPT アイテムを含む）。
 *
 * @param id          振り返り ID
 * @param title       タイトル
 * @param description 説明
 * @param kptItems    KPT アイテムのリスト
 * @param createdAt   作成日時
 * @param updatedAt   更新日時
 */
public record RetrospectiveDetailResponse(
        Long id,
        String title,
        String description,
        List<KptItemResponse> kptItems,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    /**
     * エンティティからレスポンス DTO を生成する。
     * KPT アイテムも合わせて変換する。
     *
     * @param retrospective 変換元の振り返りエンティティ
     * @return 詳細レスポンス DTO
     */
    public static RetrospectiveDetailResponse from(Retrospective retrospective) {
        List<KptItemResponse> items = retrospective.getKptItems().stream()
                .map(KptItemResponse::from)
                .toList();
        return new RetrospectiveDetailResponse(
                retrospective.getId(),
                retrospective.getTitle(),
                retrospective.getDescription(),
                items,
                retrospective.getCreatedAt(),
                retrospective.getUpdatedAt());
    }
}
