package com.myretro.repository;

import java.util.List;

import com.myretro.entity.KptItem;
import com.myretro.entity.KptType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KptItemRepository extends JpaRepository<KptItem, Long> {

    List<KptItem> findByRetrospectiveIdOrderBySortOrder(Long retrospectiveId);

    List<KptItem> findByRetrospectiveIdAndTypeOrderBySortOrder(Long retrospectiveId, KptType type);
}
