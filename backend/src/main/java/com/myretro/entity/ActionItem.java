package com.myretro.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "action_items")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
/**
 * アクションアイテムを表すエンティティ。
 * KPT の Try から派生し、ステータス管理と期限を持つ。
 */
public class ActionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kpt_item_id")
    private KptItem kptItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retrospective_id", nullable = false)
    private Retrospective retrospective;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ActionStatus status = ActionStatus.TODO;

    private LocalDate deadline;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 新規アクションアイテムを作成する。
     *
     * @param kptItem       派生元の KPT アイテム（任意）
     * @param retrospective 所属する振り返りセッション
     * @param content       内容
     * @param status        ステータス（TODO / IN_PROGRESS / DONE）
     * @param deadline      期限（任意）
     */
    public ActionItem(KptItem kptItem, Retrospective retrospective, String content, ActionStatus status, LocalDate deadline) {
        this.kptItem = kptItem;
        this.retrospective = retrospective;
        this.content = content;
        this.status = status;
        this.deadline = deadline;
    }

    /**
     * アクションアイテムの情報を更新する。
     *
     * @param content  内容
     * @param status   ステータス（TODO / IN_PROGRESS / DONE）
     * @param deadline 期限（任意）
     */
    public void update(String content, ActionStatus status, LocalDate deadline) {
        this.content = content;
        this.status = status;
        this.deadline = deadline;
    }
}
