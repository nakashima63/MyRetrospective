-- =============================================
-- V001: 初期スキーマ作成
-- テーブル: users, retrospectives, kpt_items, action_items
-- =============================================

-- users テーブル
CREATE TABLE users (
    id            BIGSERIAL    PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    username      VARCHAR(100) NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- retrospectives テーブル
CREATE TABLE retrospectives (
    id          BIGSERIAL    PRIMARY KEY,
    user_id     BIGINT       NOT NULL REFERENCES users(id),
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_retrospectives_user_id ON retrospectives(user_id);

-- kpt_items テーブル
CREATE TABLE kpt_items (
    id                BIGSERIAL   PRIMARY KEY,
    retrospective_id  BIGINT      NOT NULL REFERENCES retrospectives(id),
    type              VARCHAR(10) NOT NULL,
    content           TEXT        NOT NULL,
    sort_order        INTEGER     NOT NULL DEFAULT 0,
    created_at        TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_kpt_type CHECK (type IN ('KEEP', 'PROBLEM', 'TRY'))
);

CREATE INDEX idx_kpt_items_retrospective_id ON kpt_items(retrospective_id);

-- action_items テーブル
CREATE TABLE action_items (
    id                BIGSERIAL    PRIMARY KEY,
    kpt_item_id       BIGINT       REFERENCES kpt_items(id),
    retrospective_id  BIGINT       NOT NULL REFERENCES retrospectives(id),
    content           TEXT         NOT NULL,
    status            VARCHAR(20)  NOT NULL DEFAULT 'TODO',
    deadline          DATE,
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_action_status CHECK (status IN ('TODO', 'IN_PROGRESS', 'DONE'))
);

CREATE INDEX idx_action_items_retrospective_id ON action_items(retrospective_id);
CREATE INDEX idx_action_items_kpt_item_id ON action_items(kpt_item_id);
