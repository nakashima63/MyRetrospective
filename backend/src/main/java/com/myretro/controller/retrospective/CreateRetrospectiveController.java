package com.myretro.controller.retrospective;

import com.myretro.dto.retrospective.CreateRetrospectiveRequest;
import com.myretro.dto.retrospective.RetrospectiveResponse;
import com.myretro.entity.Retrospective;
import com.myretro.service.retrospective.CreateRetrospectiveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 振り返りを新規作成する REST コントローラー。
 */
@RestController
@RequestMapping("/api/retrospectives")
@RequiredArgsConstructor
public class CreateRetrospectiveController {

    private final CreateRetrospectiveService createRetrospectiveService;

    /**
     * 振り返りを新規作成する。
     *
     * @param userId  ログインユーザー ID
     * @param request 作成リクエスト
     * @return 作成された振り返り（201 Created）
     */
    @PostMapping
    public ResponseEntity<RetrospectiveResponse> handle(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateRetrospectiveRequest request) {
        Retrospective retrospective = createRetrospectiveService.create(
                userId, request.title(), request.description());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RetrospectiveResponse.from(retrospective));
    }
}
