package com.myretro.repository;

import java.util.List;
import java.util.Optional;

import com.myretro.entity.Retrospective;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 振り返りセッションのリポジトリ。
 * Spring Data JPA が自動的にクエリメソッドを生成する。
 */
public interface RetrospectiveRepository extends JpaRepository<Retrospective, Long> {

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
}
