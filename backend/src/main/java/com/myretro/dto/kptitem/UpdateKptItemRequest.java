package com.myretro.dto.kptitem;

import com.myretro.entity.KptType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * KPT アイテム更新リクエスト DTO。
 *
 * @param type      KPT 種別（KEEP / PROBLEM / TRY、必須）
 * @param content   内容（必須）
 * @param sortOrder 並び順（任意）
 */
public record UpdateKptItemRequest(
        @NotNull KptType type,
        @NotBlank String content,
        Integer sortOrder) {
}
