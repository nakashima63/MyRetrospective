package com.myretro.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

/**
 * KPT アイテムの並び順変更リクエスト DTO。
 *
 * @param items 並び順を変更するアイテムのリスト（1件以上必須）
 */
public record ReorderRequest(
        @NotEmpty @Valid List<ReorderItem> items) {
}
