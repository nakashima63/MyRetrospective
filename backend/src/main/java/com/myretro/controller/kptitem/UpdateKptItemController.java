package com.myretro.controller.kptitem;

import com.myretro.dto.kptitem.KptItemResponse;
import com.myretro.dto.kptitem.UpdateKptItemRequest;
import com.myretro.entity.KptItem;
import com.myretro.service.kptitem.UpdateKptItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * KPT アイテムを更新する REST コントローラー。
 */
@RestController
@RequestMapping("/api/retrospectives/{retrospectiveId}/items")
@RequiredArgsConstructor
public class UpdateKptItemController {

    private final UpdateKptItemService updateKptItemService;

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
    public ResponseEntity<KptItemResponse> handle(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long retrospectiveId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateKptItemRequest request) {
        KptItem kptItem = updateKptItemService.update(
                retrospectiveId, itemId, userId, request.type(), request.content(), request.sortOrder());
        return ResponseEntity.ok(KptItemResponse.from(kptItem));
    }
}
