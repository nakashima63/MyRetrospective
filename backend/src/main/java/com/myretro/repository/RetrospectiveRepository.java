package com.myretro.repository;

import java.util.List;
import java.util.Optional;

import com.myretro.entity.Retrospective;

/**
 * 振り返りセッションのドメインリポジトリインターフェース。
 * Service 層はこのインターフェースに依存し、JPA 実装から分離する。
 */
public interface RetrospectiveRepository {

    /**
     * 指定された ID とユーザー ID に一致する振り返りを取得する。
     *
     * @param id     振り返り ID
     * @param userId ユーザー ID
     * @return 振り返り（存在しない場合は空）
     */
    Optional<Retrospective> findByIdAndUserId(Long id, Long userId);

    /**
     * 指定ユーザーの振り返り一覧を作成日時の降順で取得する。
     *
     * @param userId ユーザー ID
     * @return 振り返りのリスト
     */
    List<Retrospective> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 振り返りを保存する。
     *
     * @param retrospective 保存する振り返りエンティティ
     * @return 保存された振り返り
     */
    Retrospective save(Retrospective retrospective);

    /**
     * 振り返りを削除する。
     *
     * @param retrospective 削除する振り返りエンティティ
     */
    void delete(Retrospective retrospective);
}
