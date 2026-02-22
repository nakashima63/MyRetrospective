package com.myretro.entity;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ActionItemTest {

    private final User user = new User("test@example.com", "hashed_password", "テストユーザー");
    private final Retrospective retrospective = new Retrospective(user, "Sprint 1", null);
    private final KptItem kptItem = new KptItem(retrospective, KptType.TRY, "テスト自動化を導入する", 0);

    @Test
    void コンストラクタで全フィールドが初期化される() {
        var deadline = LocalDate.of(2026, 3, 31);
        var action = new ActionItem(kptItem, retrospective, "Vitest を導入する", ActionStatus.TODO, deadline);

        assertThat(action.getKptItem()).isEqualTo(kptItem);
        assertThat(action.getRetrospective()).isEqualTo(retrospective);
        assertThat(action.getContent()).isEqualTo("Vitest を導入する");
        assertThat(action.getStatus()).isEqualTo(ActionStatus.TODO);
        assertThat(action.getDeadline()).isEqualTo(deadline);
        assertThat(action.getId()).isNull();
    }

    @Test
    void コンストラクタでkptItemにnullを渡せる() {
        var action = new ActionItem(null, retrospective, "独立したアクション", ActionStatus.TODO, null);

        assertThat(action.getKptItem()).isNull();
    }

    @Test
    void コンストラクタでdeadlineにnullを渡せる() {
        var action = new ActionItem(kptItem, retrospective, "期限なしアクション", ActionStatus.TODO, null);

        assertThat(action.getDeadline()).isNull();
    }

    @Test
    void updateで全フィールドが更新される() {
        var action = new ActionItem(kptItem, retrospective, "旧内容", ActionStatus.TODO, null);
        var newDeadline = LocalDate.of(2026, 6, 30);

        action.update("新内容", ActionStatus.IN_PROGRESS, newDeadline);

        assertThat(action.getContent()).isEqualTo("新内容");
        assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
        assertThat(action.getDeadline()).isEqualTo(newDeadline);
    }

    @Test
    void updateで変更不要なフィールドは元の値を渡せば維持される() {
        var deadline = LocalDate.of(2026, 3, 31);
        var action = new ActionItem(kptItem, retrospective, "内容", ActionStatus.TODO, deadline);

        action.update("内容", ActionStatus.DONE, deadline);

        assertThat(action.getContent()).isEqualTo("内容");
        assertThat(action.getStatus()).isEqualTo(ActionStatus.DONE);
        assertThat(action.getDeadline()).isEqualTo(deadline);
    }

    @Test
    void updateでretrospectiveとkptItemは変更されない() {
        var action = new ActionItem(kptItem, retrospective, "内容", ActionStatus.TODO, null);

        action.update("新内容", ActionStatus.DONE, LocalDate.now());

        assertThat(action.getRetrospective()).isEqualTo(retrospective);
        assertThat(action.getKptItem()).isEqualTo(kptItem);
    }

    @Test
    void ステータスを全種別に遷移できる() {
        var action = new ActionItem(kptItem, retrospective, "内容", ActionStatus.TODO, null);

        action.update("内容", ActionStatus.IN_PROGRESS, null);
        assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);

        action.update("内容", ActionStatus.DONE, null);
        assertThat(action.getStatus()).isEqualTo(ActionStatus.DONE);
    }
}
