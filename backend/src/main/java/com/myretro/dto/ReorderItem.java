package com.myretro.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 並び順変更の個別アイテム DTO。
 *
 * @param id        KPT アイテム ID（必須）
 * @param sortOrder 新しい並び順（必須）
 */
public record ReorderItem(
        @NotNull Long id,
        @NotNull Integer sortOrder) {
}
