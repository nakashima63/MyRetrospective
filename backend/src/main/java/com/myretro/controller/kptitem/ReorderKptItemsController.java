package com.myretro.controller.kptitem;

import com.myretro.dto.kptitem.ReorderRequest;
import com.myretro.service.kptitem.ReorderKptItemsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * KPT アイテムの並び順を一括変更する REST コントローラー。
 */
@RestController
@RequestMapping("/api/retrospectives/{retrospectiveId}/items")
@RequiredArgsConstructor
public class ReorderKptItemsController {

    private final ReorderKptItemsService reorderKptItemsService;

    /**
     * KPT アイテムの並び順を一括変更する。
     *
     * @param userId          ログインユーザー ID
     * @param retrospectiveId 振り返り ID
     * @param request         並び順変更リクエスト
     * @return 200 OK
     */
    @PatchMapping("/reorder")
    public ResponseEntity<Void> handle(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long retrospectiveId,
            @Valid @RequestBody ReorderRequest request) {
        reorderKptItemsService.reorder(retrospectiveId, userId, request.items());
        return ResponseEntity.ok().build();
    }
}
