package com.myretro.controller.kptitem;

import com.myretro.service.kptitem.DeleteKptItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * KPT アイテムを削除する REST コントローラー。
 */
@RestController
@RequestMapping("/api/retrospectives/{retrospectiveId}/items")
@RequiredArgsConstructor
public class DeleteKptItemController {

    private final DeleteKptItemService deleteKptItemService;

    /**
     * KPT アイテムを削除する。
     *
     * @param userId          ログインユーザー ID
     * @param retrospectiveId 振り返り ID
     * @param itemId          KPT アイテム ID
     * @return 204 No Content
     */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> handle(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long retrospectiveId,
            @PathVariable Long itemId) {
        deleteKptItemService.delete(retrospectiveId, itemId, userId);
        return ResponseEntity.noContent().build();
    }
}
