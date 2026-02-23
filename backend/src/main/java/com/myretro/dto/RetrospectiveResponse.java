package com.myretro.dto;

import java.time.LocalDateTime;

import com.myretro.entity.Retrospective;

/**
 * 振り返りレスポンス DTO（一覧・作成・更新時に使用）。
 *
 * @param id          振り返り ID
 * @param title       タイトル
 * @param description 説明
 * @param createdAt   作成日時
 * @param updatedAt   更新日時
 */
public record RetrospectiveResponse(
        Long id,
        String title,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    /**
     * エンティティからレスポンス DTO を生成する。
     *
     * @param retrospective 変換元の振り返りエンティティ
     * @return レスポンス DTO
     */
    public static RetrospectiveResponse from(Retrospective retrospective) {
        return new RetrospectiveResponse(
                retrospective.getId(),
                retrospective.getTitle(),
                retrospective.getDescription(),
                retrospective.getCreatedAt(),
                retrospective.getUpdatedAt());
    }
}
