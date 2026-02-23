package com.myretro.repository;

import java.util.List;
import java.util.Optional;

import com.myretro.entity.KptItem;

/**
 * KPT アイテムのドメインリポジトリインターフェース。
 * Service 層はこのインターフェースに依存し、JPA 実装から分離する。
 */
public interface KptItemRepository {

    /**
     * 指定された振り返りに属する KPT アイテムを並び順で取得する。
     *
     * @param retrospectiveId 振り返り ID
     * @return KPT アイテムのリスト（sortOrder 昇順）
     */
    List<KptItem> findByRetrospectiveIdOrderBySortOrder(Long retrospectiveId);

    /**
     * 指定された ID リストに一致する KPT アイテムをすべて取得する。
     *
     * @param ids KPT アイテム ID のリスト
     * @return KPT アイテムのリスト
     */
    List<KptItem> findAllById(Iterable<Long> ids);

    /**
     * 指定された ID の KPT アイテムを取得する。
     *
     * @param id KPT アイテム ID
     * @return KPT アイテム（存在しない場合は空）
     */
    Optional<KptItem> findById(Long id);

    /**
     * KPT アイテムを保存する。
     *
     * @param kptItem 保存する KPT アイテム
     * @return 保存された KPT アイテム
     */
    KptItem save(KptItem kptItem);

    /**
     * KPT アイテムを削除する。
     *
     * @param kptItem 削除する KPT アイテム
     */
    void delete(KptItem kptItem);
}
