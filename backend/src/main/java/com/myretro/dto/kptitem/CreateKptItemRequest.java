package com.myretro.dto.kptitem;

import com.myretro.entity.KptType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * KPT アイテム作成リクエスト DTO。
 *
 * @param type      KPT 種別（KEEP / PROBLEM / TRY、必須）
 * @param content   内容（必須）
 * @param sortOrder 並び順（任意、デフォルト 0）
 */
public record CreateKptItemRequest(
        @NotNull KptType type,
        @NotBlank String content,
        Integer sortOrder) {
}
