package com.myretro.repository;

import java.util.List;
import java.util.Optional;

import com.myretro.entity.Retrospective;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * {@link RetrospectiveRepository} の JPA 実装。
 * Spring Data JPA が自動的にクエリメソッドを生成する。
 */
public interface JpaRetrospectiveRepository
        extends JpaRepository<Retrospective, Long>, RetrospectiveRepository {

    @Override
    Optional<Retrospective> findByIdAndUserId(Long id, Long userId);

    @Override
    List<Retrospective> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Override
    Retrospective save(Retrospective retrospective);
}
