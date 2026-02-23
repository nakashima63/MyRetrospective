package com.myretro.controller.retrospective;

import com.myretro.dto.retrospective.RetrospectiveDetailResponse;
import com.myretro.entity.Retrospective;
import com.myretro.service.retrospective.GetRetrospectiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 振り返りの詳細を取得する REST コントローラー。
 */
@RestController
@RequestMapping("/api/retrospectives")
@RequiredArgsConstructor
public class GetRetrospectiveController {

    private final GetRetrospectiveService getRetrospectiveService;

    /**
     * 振り返りの詳細を取得する（KPT アイテムを含む）。
     *
     * @param userId ログインユーザー ID
     * @param id     振り返り ID
     * @return 振り返り詳細（200 OK）
     */
    @GetMapping("/{id}")
    public ResponseEntity<RetrospectiveDetailResponse> handle(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        Retrospective retrospective = getRetrospectiveService.get(id, userId);
        return ResponseEntity.ok(RetrospectiveDetailResponse.from(retrospective));
    }
}
