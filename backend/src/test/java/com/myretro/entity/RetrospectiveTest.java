package com.myretro.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RetrospectiveTest {

    private final User user = new User("test@example.com", "hashed_password", "テストユーザー");

    @Test
    void コンストラクタで全フィールドが初期化される() {
        var retro = new Retrospective(user, "Sprint 1 振り返り", "1月の振り返り");

        assertThat(retro.getUser()).isEqualTo(user);
        assertThat(retro.getTitle()).isEqualTo("Sprint 1 振り返り");
        assertThat(retro.getDescription()).isEqualTo("1月の振り返り");
        assertThat(retro.getId()).isNull();
        assertThat(retro.getKptItems()).isEmpty();
        assertThat(retro.getActionItems()).isEmpty();
    }

    @Test
    void コンストラクタでdescriptionにnullを渡せる() {
        var retro = new Retrospective(user, "Sprint 1 振り返り", null);

        assertThat(retro.getDescription()).isNull();
    }

    @Test
    void updateで全フィールドが更新される() {
        var retro = new Retrospective(user, "旧タイトル", "旧説明");

        retro.update("新タイトル", "新説明");

        assertThat(retro.getTitle()).isEqualTo("新タイトル");
        assertThat(retro.getDescription()).isEqualTo("新説明");
    }

    @Test
    void updateで変更不要なフィールドは元の値を渡せば維持される() {
        var retro = new Retrospective(user, "タイトル", "旧説明");

        retro.update("タイトル", "新説明");

        assertThat(retro.getTitle()).isEqualTo("タイトル");
        assertThat(retro.getDescription()).isEqualTo("新説明");
    }

    @Test
    void updateでuserは変更されない() {
        var retro = new Retrospective(user, "タイトル", "説明");

        retro.update("新タイトル", "新説明");

        assertThat(retro.getUser()).isEqualTo(user);
    }
}
