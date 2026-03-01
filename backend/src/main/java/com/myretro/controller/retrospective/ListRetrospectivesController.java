package com.myretro.controller.retrospective;

import java.util.List;

import com.myretro.dto.retrospective.RetrospectiveResponse;
import com.myretro.service.retrospective.ListRetrospectivesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ログインユーザーの振り返り一覧を取得する REST コントローラー。
 */
@RestController
@RequestMapping("/api/retrospectives")
@RequiredArgsConstructor
public class ListRetrospectivesController {

    private final ListRetrospectivesService listRetrospectivesService;

    /**
     * ログインユーザーの振り返り一覧を取得する。
     *
     * @param userId ログインユーザー ID
     * @return 振り返り一覧（200 OK）
     */
    @GetMapping
    public ResponseEntity<List<RetrospectiveResponse>> handle(
            @AuthenticationPrincipal Long userId) {
        List<RetrospectiveResponse> responses = listRetrospectivesService.list(userId).stream()
                .map(RetrospectiveResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
