package com.myretro.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "kpt_items")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
/**
 * KPT（Keep / Problem / Try）の各項目を表すエンティティ。
 * 振り返りセッションに紐づき、種別ごとに並び順を持つ。
 */
public class KptItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retrospective_id", nullable = false)
    private Retrospective retrospective;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private KptType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @OneToMany(mappedBy = "kptItem")
    private List<ActionItem> actionItems = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 新規 KPT アイテムを作成する。
     *
     * @param retrospective 所属する振り返りセッション
     * @param type          KPT 種別（KEEP / PROBLEM / TRY）
     * @param content       内容
     * @param sortOrder     並び順
     */
    public KptItem(Retrospective retrospective, KptType type, String content, Integer sortOrder) {
        this.retrospective = retrospective;
        this.type = type;
        this.content = content;
        this.sortOrder = sortOrder;
    }

    /**
     * KPT アイテムの情報を更新する。
     *
     * @param type      KPT 種別（KEEP / PROBLEM / TRY）
     * @param content   内容
     * @param sortOrder 並び順
     */
    public void update(KptType type, String content, Integer sortOrder) {
        this.type = type;
        this.content = content;
        this.sortOrder = sortOrder;
    }
}
