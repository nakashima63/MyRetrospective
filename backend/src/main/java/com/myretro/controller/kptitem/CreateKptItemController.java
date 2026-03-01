package com.myretro.controller.kptitem;

import com.myretro.dto.kptitem.CreateKptItemRequest;
import com.myretro.dto.kptitem.KptItemResponse;
import com.myretro.entity.KptItem;
import com.myretro.service.kptitem.CreateKptItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * KPT アイテムを新規作成する REST コントローラー。
 */
@RestController
@RequestMapping("/api/retrospectives/{retrospectiveId}/items")
@RequiredArgsConstructor
public class CreateKptItemController {

    private final CreateKptItemService createKptItemService;

    /**
     * KPT アイテムを新規作成する。
     *
     * @param userId          ログインユーザー ID
     * @param retrospectiveId 振り返り ID
     * @param request         作成リクエスト
     * @return 作成された KPT アイテム（201 Created）
     */
    @PostMapping
    public ResponseEntity<KptItemResponse> handle(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long retrospectiveId,
            @Valid @RequestBody CreateKptItemRequest request) {
        KptItem kptItem = createKptItemService.create(
                retrospectiveId, userId, request.type(), request.content(), request.sortOrder());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(KptItemResponse.from(kptItem));
    }
}
