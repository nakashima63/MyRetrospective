package com.myretro.repository;

import java.util.List;

import com.myretro.entity.KptItem;
import com.myretro.entity.KptType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * {@link KptItemRepository} の JPA 実装。
 * Spring Data JPA が自動的にクエリメソッドを生成する。
 */
public interface JpaKptItemRepository
        extends JpaRepository<KptItem, Long>, KptItemRepository {

    @Override
    List<KptItem> findByRetrospectiveIdOrderBySortOrder(Long retrospectiveId);

    @Override
    KptItem save(KptItem kptItem);

    /**
     * 指定された振り返りの指定タイプの KPT アイテムを並び順で取得する。
     *
     * @param retrospectiveId 振り返り ID
     * @param type            KPT タイプ（KEEP / PROBLEM / TRY）
     * @return KPT アイテムのリスト（sortOrder 昇順）
     */
    List<KptItem> findByRetrospectiveIdAndTypeOrderBySortOrder(Long retrospectiveId, KptType type);
}
