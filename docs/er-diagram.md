# ER 図 — MyRetrospective

```mermaid
erDiagram
    users ||--o{ retrospectives : "has many"
    retrospectives ||--o{ kpt_items : "has many"
    retrospectives ||--o{ action_items : "has many"
    kpt_items ||--o{ action_items : "generates"

    users {
        bigserial id PK
        varchar(255) email UK "NOT NULL"
        varchar(255) password_hash "NOT NULL"
        varchar(100) username "NOT NULL"
        timestamp created_at "NOT NULL, DEFAULT NOW()"
        timestamp updated_at "NOT NULL, DEFAULT NOW()"
    }

    retrospectives {
        bigserial id PK
        bigint user_id FK "NOT NULL → users(id)"
        varchar(255) title "NOT NULL"
        text description
        timestamp created_at "NOT NULL, DEFAULT NOW()"
        timestamp updated_at "NOT NULL, DEFAULT NOW()"
    }

    kpt_items {
        bigserial id PK
        bigint retrospective_id FK "NOT NULL → retrospectives(id)"
        varchar(10) type "NOT NULL (KEEP/PROBLEM/TRY)"
        text content "NOT NULL"
        integer sort_order "NOT NULL, DEFAULT 0"
        timestamp created_at "NOT NULL, DEFAULT NOW()"
        timestamp updated_at "NOT NULL, DEFAULT NOW()"
    }

    action_items {
        bigserial id PK
        bigint kpt_item_id FK "→ kpt_items(id)"
        bigint retrospective_id FK "NOT NULL → retrospectives(id)"
        text content "NOT NULL"
        varchar(20) status "NOT NULL, DEFAULT 'TODO'"
        date deadline
        timestamp created_at "NOT NULL, DEFAULT NOW()"
        timestamp updated_at "NOT NULL, DEFAULT NOW()"
    }
```

## テーブル概要

| テーブル | 説明 |
|---|---|
| `users` | ユーザーアカウント。メール + パスワード認証 |
| `retrospectives` | 振り返りセッション。ユーザーごとに複数作成可能 |
| `kpt_items` | KPT（Keep / Problem / Try）の各項目 |
| `action_items` | Try から派生するアクションアイテム（Phase 3 で本格利用） |

## インデックス

| テーブル | カラム | 用途 |
|---|---|---|
| `retrospectives` | `user_id` | ユーザーの振り返り一覧取得 |
| `kpt_items` | `retrospective_id` | 振り返りの KPT 一覧取得 |
| `action_items` | `retrospective_id` | 振り返りのアクション一覧取得 |
| `action_items` | `kpt_item_id` | KPT アイテムに紐づくアクション取得 |
