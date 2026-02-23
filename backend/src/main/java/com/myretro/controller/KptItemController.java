package com.myretro.controller;

import com.myretro.dto.CreateKptItemRequest;
import com.myretro.dto.KptItemResponse;
import com.myretro.dto.ReorderRequest;
import com.myretro.dto.UpdateKptItemRequest;
import com.myretro.entity.KptItem;
import com.myretro.service.CreateKptItemService;
import com.myretro.service.DeleteKptItemService;
import com.myretro.service.ReorderKptItemsService;
import com.myretro.service.UpdateKptItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * KPT アイテムの CRUD と並び順変更を提供する REST コントローラー。
 * 振り返りセッションのサブリソースとして動作する。
 */
@RestController
@RequestMapping("/api/retrospectives/{retrospectiveId}/items")
@RequiredArgsConstructor
public class KptItemController {

    private final CreateKptItemService createKptItemService;
    private final UpdateKptItemService updateKptItemService;
    private final DeleteKptItemService deleteKptItemService;
    private final ReorderKptItemsService reorderKptItemsService;

    /**
     * KPT アイテムを新規作成する。
     *
     * @param userId          ログインユーザー ID
     * @param retrospectiveId 振り返り ID
     * @param request         作成リクエスト
     * @return 作成された KPT アイテム（201 Created）
     */
    @PostMapping
    public ResponseEntity<KptItemResponse> create(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long retrospectiveId,
            @Valid @RequestBody CreateKptItemRequest request) {
        KptItem kptItem = createKptItemService.create(
                retrospectiveId, userId, request.type(), request.content(), request.sortOrder());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(KptItemResponse.from(kptItem));
    }

    /**
     * KPT アイテムを更新する。
     *
     * @param userId          ログインユーザー ID
     * @param retrospectiveId 振り返り ID
     * @param itemId          KPT アイテム ID
     * @param request         更新リクエスト
     * @return 更新された KPT アイテム（200 OK）
     */
    @PutMapping("/{itemId}")
    public ResponseEntity<KptItemResponse> update(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long retrospectiveId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateKptItemRequest request) {
        KptItem kptItem = updateKptItemService.update(
                retrospectiveId, itemId, userId, request.type(), request.content(), request.sortOrder());
        return ResponseEntity.ok(KptItemResponse.from(kptItem));
    }

    /**
     * KPT アイテムを削除する。
     *
     * @param userId          ログインユーザー ID
     * @param retrospectiveId 振り返り ID
     * @param itemId          KPT アイテム ID
     * @return 204 No Content
     */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long retrospectiveId,
            @PathVariable Long itemId) {
        deleteKptItemService.delete(retrospectiveId, itemId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * KPT アイテムの並び順を一括変更する。
     *
     * @param userId          ログインユーザー ID
     * @param retrospectiveId 振り返り ID
     * @param request         並び順変更リクエスト
     * @return 200 OK
     */
    @PatchMapping("/reorder")
    public ResponseEntity<Void> reorder(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long retrospectiveId,
            @Valid @RequestBody ReorderRequest request) {
        reorderKptItemsService.reorder(retrospectiveId, userId, request.items());
        return ResponseEntity.ok().build();
    }
}
