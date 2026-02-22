package com.myretro.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void コンストラクタで全フィールドが初期化される() {
        var user = new User("test@example.com", "hashed_password", "テストユーザー");

        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getPasswordHash()).isEqualTo("hashed_password");
        assertThat(user.getUsername()).isEqualTo("テストユーザー");
        assertThat(user.getId()).isNull();
        assertThat(user.getCreatedAt()).isNull();
        assertThat(user.getUpdatedAt()).isNull();
    }

    @Test
    void updateで全フィールドが更新される() {
        var user = new User("old@example.com", "old_hash", "旧ユーザー");

        user.update("new@example.com", "new_hash", "新ユーザー");

        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(user.getPasswordHash()).isEqualTo("new_hash");
        assertThat(user.getUsername()).isEqualTo("新ユーザー");
    }

    @Test
    void updateで変更不要なフィールドは元の値を渡せば維持される() {
        var user = new User("test@example.com", "hashed_password", "テストユーザー");

        user.update("test@example.com", "hashed_password", "新しい名前");

        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getPasswordHash()).isEqualTo("hashed_password");
        assertThat(user.getUsername()).isEqualTo("新しい名前");
    }
}
