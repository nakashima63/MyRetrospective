package com.myretro.repository;

import java.util.List;

import com.myretro.entity.KptItem;
import com.myretro.entity.KptType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * KPT アイテムのリポジトリ。
 * Spring Data JPA が自動的にクエリメソッドを生成する。
 */
public interface KptItemRepository extends JpaRepository<KptItem, Long> {

    /**
     * 指定された振り返りに属する KPT アイテムを並び順で取得する。
     *
     * @param retrospectiveId 振り返り ID
     * @return KPT アイテムのリスト（sortOrder 昇順）
     */
    List<KptItem> findByRetrospectiveIdOrderBySortOrder(Long retrospectiveId);

    /**
     * 指定された振り返りの指定タイプの KPT アイテムを並び順で取得する。
     *
     * @param retrospectiveId 振り返り ID
     * @param type            KPT タイプ（KEEP / PROBLEM / TRY）
     * @return KPT アイテムのリスト（sortOrder 昇順）
     */
    List<KptItem> findByRetrospectiveIdAndTypeOrderBySortOrder(Long retrospectiveId, KptType type);
}
