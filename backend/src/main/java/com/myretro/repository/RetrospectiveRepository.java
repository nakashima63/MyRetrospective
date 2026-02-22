package com.myretro.repository;

import java.util.List;

import com.myretro.entity.Retrospective;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetrospectiveRepository extends JpaRepository<Retrospective, Long> {

    List<Retrospective> findByUserIdOrderByCreatedAtDesc(Long userId);
}
