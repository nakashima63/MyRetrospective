package com.myretro.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
/**
 * ユーザーアカウントを表すエンティティ。
 * メールアドレスとパスワードによる認証に使用する。
 */
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String username;

    @OneToMany(mappedBy = "user")
    private List<Retrospective> retrospectives = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 新規ユーザーを作成する。
     *
     * @param email        メールアドレス
     * @param passwordHash ハッシュ化済みパスワード
     * @param username     ユーザー名
     */
    public User(String email, String passwordHash, String username) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.username = username;
    }

    /**
     * ユーザー情報を更新する。
     *
     * @param email        メールアドレス
     * @param passwordHash ハッシュ化済みパスワード
     * @param username     ユーザー名
     */
    public void update(String email, String passwordHash, String username) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.username = username;
    }
}
