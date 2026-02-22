package com.myretro.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
@Table(name = "retrospectives")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
/**
 * 振り返りセッションを表すエンティティ。
 * ユーザーごとに複数作成でき、KPT アイテムとアクションアイテムを保持する。
 */
public class Retrospective {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    private String description;

    @OneToMany(mappedBy = "retrospective")
    private List<KptItem> kptItems = new ArrayList<>();

    @OneToMany(mappedBy = "retrospective")
    private List<ActionItem> actionItems = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 新規振り返りセッションを作成する。
     *
     * @param user        所有ユーザー
     * @param title       タイトル
     * @param description 説明（任意）
     */
    public Retrospective(User user, String title, String description) {
        this.user = user;
        this.title = title;
        this.description = description;
    }

    /**
     * 振り返りセッションの情報を更新する。
     *
     * @param title       タイトル
     * @param description 説明（任意）
     */
    public void update(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
