package com.myretro.controller.retrospective;

import com.myretro.dto.retrospective.RetrospectiveResponse;
import com.myretro.dto.retrospective.UpdateRetrospectiveRequest;
import com.myretro.entity.Retrospective;
import com.myretro.service.retrospective.UpdateRetrospectiveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 振り返りを更新する REST コントローラー。
 */
@RestController
@RequestMapping("/api/retrospectives")
@RequiredArgsConstructor
public class UpdateRetrospectiveController {

    private final UpdateRetrospectiveService updateRetrospectiveService;

    /**
     * 振り返りを更新する。
     *
     * @param userId  ログインユーザー ID
     * @param id      振り返り ID
     * @param request 更新リクエスト
     * @return 更新された振り返り（200 OK）
     */
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<RetrospectiveResponse> handle(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateRetrospectiveRequest request) {
        Retrospective retrospective = updateRetrospectiveService.update(
                id, userId, request.title(), request.description());
        return ResponseEntity.ok(RetrospectiveResponse.from(retrospective));
    }
}
