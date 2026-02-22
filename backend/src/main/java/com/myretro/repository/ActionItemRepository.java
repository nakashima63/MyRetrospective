package com.myretro.repository;

import java.util.List;

import com.myretro.entity.ActionItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionItemRepository extends JpaRepository<ActionItem, Long> {

    List<ActionItem> findByRetrospectiveId(Long retrospectiveId);
}
