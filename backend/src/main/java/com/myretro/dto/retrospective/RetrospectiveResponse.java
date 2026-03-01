package com.myretro.dto.retrospective;

import java.time.LocalDateTime;

import com.myretro.entity.KptType;
import com.myretro.entity.Retrospective;

/**
 * 振り返りレスポンス DTO（一覧・作成・更新時に使用）。
 *
 * @param id           振り返り ID
 * @param title        タイトル
 * @param description  説明
 * @param keepCount    Keep アイテム数
 * @param problemCount Problem アイテム数
 * @param tryCount     Try アイテム数
 * @param createdAt    作成日時
 * @param updatedAt    更新日時
 */
public record RetrospectiveResponse(
        Long id,
        String title,
        String description,
        long keepCount,
        long problemCount,
        long tryCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    /**
     * エンティティからレスポンス DTO を生成する。
     * KPT アイテムのカウントを種別ごとに集計する。
     *
     * @param retrospective 変換元の振り返りエンティティ
     * @return レスポンス DTO
     */
    public static RetrospectiveResponse from(Retrospective retrospective) {
        long keepCount = retrospective.getKptItems().stream()
                .filter(item -> item.getType() == KptType.KEEP)
                .count();
        long problemCount = retrospective.getKptItems().stream()
                .filter(item -> item.getType() == KptType.PROBLEM)
                .count();
        long tryCount = retrospective.getKptItems().stream()
                .filter(item -> item.getType() == KptType.TRY)
                .count();

        return new RetrospectiveResponse(
                retrospective.getId(),
                retrospective.getTitle(),
                retrospective.getDescription(),
                keepCount,
                problemCount,
                tryCount,
                retrospective.getCreatedAt(),
                retrospective.getUpdatedAt());
    }
}
