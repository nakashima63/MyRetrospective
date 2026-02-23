package com.myretro.controller;

import java.util.List;

import com.myretro.dto.CreateRetrospectiveRequest;
import com.myretro.dto.RetrospectiveDetailResponse;
import com.myretro.dto.RetrospectiveResponse;
import com.myretro.dto.UpdateRetrospectiveRequest;
import com.myretro.entity.Retrospective;
import com.myretro.service.CreateRetrospectiveService;
import com.myretro.service.DeleteRetrospectiveService;
import com.myretro.service.GetRetrospectiveService;
import com.myretro.service.ListRetrospectivesService;
import com.myretro.service.UpdateRetrospectiveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 振り返りセッションの CRUD を提供する REST コントローラー。
 */
@RestController
@RequestMapping("/api/retrospectives")
@RequiredArgsConstructor
public class RetrospectiveController {

    private final CreateRetrospectiveService createRetrospectiveService;
    private final ListRetrospectivesService listRetrospectivesService;
    private final GetRetrospectiveService getRetrospectiveService;
    private final UpdateRetrospectiveService updateRetrospectiveService;
    private final DeleteRetrospectiveService deleteRetrospectiveService;

    /**
     * 振り返りを新規作成する。
     *
     * @param userId  ログインユーザー ID
     * @param request 作成リクエスト
     * @return 作成された振り返り（201 Created）
     */
    @PostMapping
    public ResponseEntity<RetrospectiveResponse> create(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateRetrospectiveRequest request) {
        Retrospective retrospective = createRetrospectiveService.create(
                userId, request.title(), request.description());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RetrospectiveResponse.from(retrospective));
    }

    /**
     * ログインユーザーの振り返り一覧を取得する。
     *
     * @param userId ログインユーザー ID
     * @return 振り返り一覧（200 OK）
     */
    @GetMapping
    public ResponseEntity<List<RetrospectiveResponse>> list(
            @AuthenticationPrincipal Long userId) {
        List<RetrospectiveResponse> responses = listRetrospectivesService.list(userId).stream()
                .map(RetrospectiveResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    /**
     * 振り返りの詳細を取得する（KPT アイテムを含む）。
     *
     * @param userId ログインユーザー ID
     * @param id     振り返り ID
     * @return 振り返り詳細（200 OK）
     */
    @GetMapping("/{id}")
    public ResponseEntity<RetrospectiveDetailResponse> get(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        Retrospective retrospective = getRetrospectiveService.get(id, userId);
        return ResponseEntity.ok(RetrospectiveDetailResponse.from(retrospective));
    }

    /**
     * 振り返りを更新する。
     *
     * @param userId  ログインユーザー ID
     * @param id      振り返り ID
     * @param request 更新リクエスト
     * @return 更新された振り返り（200 OK）
     */
    @PutMapping("/{id}")
    public ResponseEntity<RetrospectiveResponse> update(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateRetrospectiveRequest request) {
        Retrospective retrospective = updateRetrospectiveService.update(
                id, userId, request.title(), request.description());
        return ResponseEntity.ok(RetrospectiveResponse.from(retrospective));
    }

    /**
     * 振り返りを削除する。
     *
     * @param userId ログインユーザー ID
     * @param id     振り返り ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        deleteRetrospectiveService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
