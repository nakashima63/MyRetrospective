package com.myretro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 振り返り作成リクエスト DTO。
 *
 * @param title       タイトル（必須、255文字以内）
 * @param description 説明（任意）
 */
public record CreateRetrospectiveRequest(
        @NotBlank @Size(max = 255) String title,
        String description) {
}
