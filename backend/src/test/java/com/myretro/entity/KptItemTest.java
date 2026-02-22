package com.myretro.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KptItemTest {

    private final User user = new User("test@example.com", "hashed_password", "テストユーザー");
    private final Retrospective retrospective = new Retrospective(user, "Sprint 1", null);

    @Test
    void コンストラクタで全フィールドが初期化される() {
        var item = new KptItem(retrospective, KptType.KEEP, "チーム内の連携が良かった", 1);

        assertThat(item.getRetrospective()).isEqualTo(retrospective);
        assertThat(item.getType()).isEqualTo(KptType.KEEP);
        assertThat(item.getContent()).isEqualTo("チーム内の連携が良かった");
        assertThat(item.getSortOrder()).isEqualTo(1);
        assertThat(item.getId()).isNull();
        assertThat(item.getActionItems()).isEmpty();
    }

    @Test
    void コンストラクタでKPT種別ごとに作成できる() {
        var keep = new KptItem(retrospective, KptType.KEEP, "良かったこと", 0);
        var problem = new KptItem(retrospective, KptType.PROBLEM, "課題", 0);
        var tryItem = new KptItem(retrospective, KptType.TRY, "試すこと", 0);

        assertThat(keep.getType()).isEqualTo(KptType.KEEP);
        assertThat(problem.getType()).isEqualTo(KptType.PROBLEM);
        assertThat(tryItem.getType()).isEqualTo(KptType.TRY);
    }

    @Test
    void updateで全フィールドが更新される() {
        var item = new KptItem(retrospective, KptType.KEEP, "旧内容", 0);

        item.update(KptType.PROBLEM, "新内容", 5);

        assertThat(item.getType()).isEqualTo(KptType.PROBLEM);
        assertThat(item.getContent()).isEqualTo("新内容");
        assertThat(item.getSortOrder()).isEqualTo(5);
    }

    @Test
    void updateで変更不要なフィールドは元の値を渡せば維持される() {
        var item = new KptItem(retrospective, KptType.TRY, "内容", 3);

        item.update(KptType.TRY, "内容", 10);

        assertThat(item.getType()).isEqualTo(KptType.TRY);
        assertThat(item.getContent()).isEqualTo("内容");
        assertThat(item.getSortOrder()).isEqualTo(10);
    }

    @Test
    void updateでretrospectiveは変更されない() {
        var item = new KptItem(retrospective, KptType.KEEP, "内容", 0);

        item.update(KptType.PROBLEM, "新内容", 1);

        assertThat(item.getRetrospective()).isEqualTo(retrospective);
    }
}
