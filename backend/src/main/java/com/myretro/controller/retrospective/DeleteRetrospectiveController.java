package com.myretro.controller.retrospective;

import com.myretro.service.retrospective.DeleteRetrospectiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 振り返りを削除する REST コントローラー。
 */
@RestController
@RequestMapping("/api/retrospectives")
@RequiredArgsConstructor
public class DeleteRetrospectiveController {

    private final DeleteRetrospectiveService deleteRetrospectiveService;

    /**
     * 振り返りを削除する。
     *
     * @param userId ログインユーザー ID
     * @param id     振り返り ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> handle(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        deleteRetrospectiveService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
