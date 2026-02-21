# Claude Code 活用ガイド — MyRetrospective 開発

> 振り返り特化アプリ「MyRetrospective」を Claude Code で効率的に開発するための実践ガイド

---

## 目次

1. [開発フローの全体像](#1-開発フローの全体像)
2. [プロジェクト初期セットアップ](#2-プロジェクト初期セットアップ)
3. [CLAUDE.md の設計](#3-claudemd-の設計)
4. [日常の開発ワークフロー](#4-日常の開発ワークフロー)
5. [プロンプトの書き方](#5-プロンプトの書き方)
6. [便利な機能・テクニック集](#6-便利な機能テクニック集)
7. [テストと品質管理](#7-テストと品質管理)
8. [コンテキスト管理](#8-コンテキスト管理)
9. [Hooks によるワークフロー自動化](#9-hooks-によるワークフロー自動化)
10. [MCP サーバー連携](#10-mcp-サーバー連携)
11. [CI/CD との統合](#11-cicd-との統合)
12. [よくある失敗と対策](#12-よくある失敗と対策)

---

## 1. 開発フローの全体像

Claude Code を最大限活用した開発は、以下の 4 ステップで回す。

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  1. Explore  │───▶│   2. Plan   │───▶│ 3. Implement│───▶│  4. Commit  │
│  (Plan Mode) │    │  (Plan Mode) │    │(Normal Mode)│    │(Normal Mode)│
│              │    │              │    │              │    │              │
│ コードを読む  │    │ 計画を立てる  │    │ 実装＋テスト  │    │ コミット＋PR │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
        ▲                                                        │
        └────────────────────────────────────────────────────────┘
                           /clear してから次のタスクへ
```

**Plan Mode（Shift+Tab で切替）** → 読み取り専用。安全にコードベースを探索し計画を練る。
**Normal Mode** → ファイル編集・コマンド実行が可能。実装を進める。

---

## 2. プロジェクト初期セットアップ

### Step 1: Git リポジトリ初期化

```bash
cd ~/Projects/MyRetrospective
git init
```

### Step 2: CLAUDE.md の生成

```bash
claude /init
```

`/init` はプロジェクト構造を分析し、ビルドコマンド・テストフレームワーク・コードパターンを自動検出して CLAUDE.md の雛形を生成する。

### Step 3: パーミッション設定

```bash
claude /permissions
```

安全なコマンド（lint、test、build）を許可リストに追加し、開発中の確認ダイアログを減らす。

### Step 4: Hooks の設定（任意）

```bash
claude /hooks
```

コード整形の自動実行やファイル保護ルールを設定する（[詳細は後述](#9-hooks-によるワークフロー自動化)）。

### Step 5: MCP サーバーの追加（任意）

```bash
claude mcp add --transport http github https://api.githubcopilot.com/mcp/
claude mcp list
```

---

## 3. CLAUDE.md の設計

CLAUDE.md は **Claude Code に与える永続的なコンテキスト** であり、プロジェクト全体の品質を左右する最重要ファイル。

### ファイル階層

```
MyRetrospective/
├── CLAUDE.md                      # プロジェクト全体の指示（git 管理）
├── CLAUDE.local.md                # 個人設定（.gitignore に追加）
├── .claude/
│   └── rules/
│       ├── code-style.md          # コードスタイルルール
│       ├── testing.md             # テストルール
│       └── api-design.md          # API 設計ルール
├── packages/
│   ├── frontend/
│   │   └── CLAUDE.md              # フロントエンド固有の指示
│   └── backend/
│       └── CLAUDE.md              # バックエンド固有の指示
```

**読み込み優先度**: ホームディレクトリ → 親ディレクトリ → プロジェクトルート → 子ディレクトリ（オンデマンド）

### CLAUDE.md に書くべきこと

```markdown
# MyRetrospective

## プロジェクト概要
KPT（Keep/Problem/Try）を中心としたレトロスペクティブ支援アプリ。
入力→整理→次アクション化まで一気通貫で支援する。

## 技術スタック
- Frontend: React + React Router + TypeScript
- Backend: Spling Boot
- DB: Supabase (PostgreSQL)
- 認証: Supabase Auth
- スタイル: Tailwind CSS + shadcn/ui
- テスト: Vitest + Playwright + Junit

## コマンド
- `pnpm dev` — 開発サーバー起動
- `pnpm build` — プロダクションビルド
- `pnpm test` — Vitest 実行
- `pnpm test:e2e` — Playwright E2E テスト
- `pnpm lint` — ESLint 実行
- `pnpm format` — Prettier 実行

## コード規約
- ES Modules（import/export）を使用、CommonJS は不可
- 2 スペースインデント
- コンポーネントは関数コンポーネント + hooks のみ
- 状態管理は React Server Components + Server Actions 優先
- DB アクセスは必ず Supabase クライアント経由

## テスト方針
- ビジネスロジックは Vitest で単体テスト必須
- UI コンポーネントは重要な操作フローのみ Playwright でテスト
- テスト実行後にすべて通ることを確認してからコミット

## ブランチ・コミット規約
- ブランチ: feature/*, fix/*, docs/*
- コミットメッセージ: Conventional Commits 形式
  - feat: 新機能、fix: バグ修正、docs: ドキュメント、refactor: リファクタ

## ディレクトリ構成
- src/app/ — Next.js App Router ページ
- src/components/ — 再利用可能な UI コンポーネント
- src/lib/ — ユーティリティ・ヘルパー
- src/types/ — TypeScript 型定義
- supabase/ — マイグレーション・シード
```

### 書くべきでないこと

| 書くべきでないこと | 理由 |
|---|---|
| 言語の一般的な規約 | Claude は既に知っている |
| ファイルごとの詳細説明 | コードを読めばわかる |
| 長いチュートリアル | コンテキストを浪費する |
| 頻繁に変わる情報 | メンテナンスコストが高い |
| 「きれいなコードを書け」 | 自明すぎる指示 |

### モジュール別ルール（.claude/rules/）

パス条件付きでルールを分割できる。

```markdown
# .claude/rules/api-design.md
---
paths:
  - "src/app/api/**/*.ts"
---

# API 設計ルール
- すべてのエンドポイントで入力バリデーション（zod）を実施
- エラーレスポンスは { error: string, code: string } 形式
- 認証が必要なエンドポイントは middleware で制御
```

### ファイル参照（@記法）

CLAUDE.md 内で外部ファイルを参照できる。

```markdown
API仕様は @docs/api-spec.md を参照。
パッケージ情報は @package.json を参照。
```

---

## 4. 日常の開発ワークフロー

### 基本パターン: 機能追加

```
# 1. Plan Mode で調査・計画（Shift+Tab で Plan Mode に切替）
> src/app/ のディレクトリ構成を確認して、KPT入力画面を追加する計画を立てて

# 2. 計画をレビュー・修正（Ctrl+G でエディタで計画を編集可能）

# 3. Normal Mode に切替（Shift+Tab）して実装
> 計画に従って KPT 入力画面を実装して。テストも書いて実行して

# 4. コミット
> 変更内容をコミットして

# 5. コンテキストをクリア
/clear
```

### バグ修正パターン

```
> ログイン後にリダイレクトされない問題を調査して。
  src/app/auth/ の認証フローを確認し、原因を特定して修正して。
  修正後はテストを書いて通ることを確認して。
```

### セッション管理

```bash
# 前回のセッションを再開
claude --continue

# 名前付きセッションを再開
claude --resume

# セッションに名前をつける
/rename kpt-input-feature

# 並列作業（Git Worktree）
claude --worktree feature-kpt-input
claude --worktree fix-auth-redirect
```

---

## 5. プロンプトの書き方

### 原則 1: 具体的に書く

| 悪い例 | 良い例 |
|---|---|
| KPT画面を作って | KPT（Keep/Problem/Try）の入力フォームを作って。各カテゴリにカード形式で入力でき、ドラッグで並べ替え可能にして。既存の src/components/Card.tsx のパターンに合わせて |
| テストを書いて | src/lib/kpt-parser.ts の parseKptInput 関数のテストを書いて。空入力、日本語テキスト、特殊文字を含むケースをカバーして |
| バグを直して | KPT一覧画面で「Try」カテゴリのアイテムが表示されない。src/app/retrospectives/page.tsx のフィルタリングロジックを確認して原因を特定・修正して |

### 原則 2: 検証手段を含める

```
> parseKptInput 関数を実装して。
  テストケース:
  - "Keep: チーム連携が良かった" → { category: "keep", text: "チーム連携が良かった" }
  - "" → エラーを返す
  - "Unknown: テスト" → エラーを返す
  テストを書いて実行し、すべて通ることを確認して。
```

### 原則 3: 段階的に指示する

複雑なタスクは一度に頼まず、段階を分ける。

```
# Step 1
> まず Supabase のスキーマを設計して。retrospectives テーブルと kpt_items テーブルが必要。

# Step 2（Step 1 の結果を確認してから）
> マイグレーションファイルを作成して適用して。

# Step 3
> API エンドポイントを実装して。
```

### 原則 4: コンテキストを渡す

```
# @ でファイル参照
> @src/components/Card.tsx のパターンに合わせて KptCard コンポーネントを作って

# 画像を貼り付け（UI モック）
> [スクリーンショットを貼り付け] このデザインを実装して

# エラーログをパイプ
cat build-error.log | claude -p "このビルドエラーを修正して"
```

---

## 6. 便利な機能・テクニック集

### スラッシュコマンド一覧

| コマンド | 用途 |
|---|---|
| `/init` | CLAUDE.md を自動生成 |
| `/clear` | コンテキストをリセット（タスク間で必ず使う） |
| `/compact` | コンテキストを手動で圧縮 |
| `/memory` | CLAUDE.md / メモリファイルを管理 |
| `/hooks` | Hooks を設定 |
| `/permissions` | パーミッションを管理 |
| `/context` | コンテキスト使用量を確認 |
| `/model` | モデル・思考レベルを変更 |
| `/rewind` | 直前の状態に巻き戻し |
| `/rename` | セッションに名前をつける |

### キーボードショートカット

| キー | 動作 |
|---|---|
| `Shift+Tab` | Plan Mode ↔ Normal Mode 切替 |
| `Esc` | Claude の動作を中断（コンテキストは保持） |
| `Esc` × 2 | 直前の状態に巻き戻し |
| `Ctrl+G` | 入力内容をエディタで編集 |
| `Option+T` (macOS) | Extended Thinking のオン/オフ |
| `Ctrl+O` | Verbose モード（思考過程を表示） |

### 自動メモリ（Auto Memory）

Claude は開発中に学んだことを自動で記憶する。

```
# 明示的に記憶させる
> pnpm を使うこと、npm は使わないことを覚えて
> E2E テストの前に supabase db reset が必要なことを覚えて
```

保存先: `~/.claude/projects/<project>/memory/MEMORY.md`

### Extended Thinking（深い思考モード）

複雑な設計判断やデバッグ時に有効。

```bash
# セッション中にトグル
Option+T  # macOS

# 思考レベルを調整（Opus 4.6）
/model  # → low / medium / high を選択
```

---

## 7. テストと品質管理

### テスト駆動の開発フロー

```
# 1. テストを先に書かせる
> KptParser の parseInput メソッドのテストを先に書いて。
  正常系、空入力、不正フォーマットをカバーして。

# 2. テストが失敗することを確認
> テストを実行して、すべて失敗することを確認して

# 3. 実装して通す
> テストが通るように parseInput を実装して。テストを実行して確認して。
```

### テスト生成のコツ

```
# 既存パターンに合わせる
> src/lib/__tests__/auth.test.ts のスタイルに合わせて
  kpt-parser.test.ts を作成して

# エッジケースを網羅
> parseInput の境界値テストを追加して。
  最大文字数、Unicode 絵文字、改行を含むテキストを含めて
```

### コミット前チェック

```
> lint とテストを実行して、すべて通ったらコミットして
```

---

## 8. コンテキスト管理

**最重要**: コンテキストウィンドウが埋まるとパフォーマンスが劣化する。積極的に管理する。

### 基本ルール

1. **タスクが変わったら `/clear`** — 無関係なコンテキストの蓄積を防ぐ
2. **大量出力はサブエージェントに委譲** — テスト実行やログ分析は別コンテキストで
3. **`@` でファイル参照** — Claude に都度読ませるよりトークン効率が良い
4. **`/compact` で手動圧縮** — コンテキストが膨らんできたら実行
5. **`/context` で使用量を確認** — 定期的にチェック

### サブエージェントの活用

メインのコンテキストを汚さずに調査・テスト実行を行える。

```
# 調査をサブエージェントに委任
> サブエージェントを使って、認証フローのトークンリフレッシュ処理を調査して

# テスト実行をサブエージェントに委任
> サブエージェントでテストスイートを実行して、失敗したテストだけ報告して

# 並列調査
> 認証モジュール、DB モジュール、API モジュールを並列でサブエージェントに調査させて
```

---

## 9. Hooks によるワークフロー自動化

Hooks は **毎回確実に実行すべき処理** を自動化する仕組み。

### 利用可能なイベント

| イベント | タイミング |
|---|---|
| `PreToolUse` | ツール実行前（ブロック可能） |
| `PostToolUse` | ツール実行後 |
| `UserPromptSubmit` | プロンプト送信前 |
| `SessionStart` | セッション開始時 |
| `PreCompact` | コンテキスト圧縮前 |

### 実用例: コード自動整形

ファイル編集後に自動で Prettier を実行する。

```json
{
  "hooks": {
    "PostToolUse": [
      {
        "matcher": "Edit|Write",
        "hooks": [
          {
            "type": "command",
            "command": "jq -r '.tool_input.file_path' | xargs npx prettier --write 2>/dev/null || true"
          }
        ]
      }
    ]
  }
}
```

### 実用例: 保護ファイルへの書き込みブロック

```json
{
  "hooks": {
    "PreToolUse": [
      {
        "matcher": "Edit|Write",
        "hooks": [
          {
            "type": "command",
            "command": "bash .claude/hooks/protect-files.sh"
          }
        ]
      }
    ]
  }
}
```

---

## 10. MCP サーバー連携

MCP（Model Context Protocol）でClaude Codeに外部ツール連携機能を追加できる。

### 推奨 MCP サーバー

```bash
# GitHub 連携（PR・Issue 操作）
claude mcp add --transport http github https://api.githubcopilot.com/mcp/

# Playwright（E2E テスト・ブラウザ操作）
claude mcp add playwright -- npx @anthropic-ai/mcp-playwright

# Context7（ライブラリドキュメント参照）
claude mcp add context7 -- npx -y @upstash/context7-mcp@latest

# 設定確認
claude mcp list
```

---

## 11. CI/CD との統合

### ヘッドレスモードでの自動レビュー

```bash
# PR のコードレビュー
claude -p "このPRの変更をレビューして、セキュリティとパフォーマンスの問題を報告して" \
  --output-format json > review.json

# lint チェック
claude -p "main ブランチとの差分を確認し、コード品質の問題を報告して" \
  --output-format json >> lint-results.json
```

### GitHub Actions での活用

```yaml
# .github/workflows/claude-review.yml
name: Claude Code Review
on: [pull_request]
jobs:
  review:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - run: |
          claude -p "Review this PR for code quality and security" \
            --output-format stream-json
```

### カスタム Skills でワークフローを定型化

```markdown
# .claude/skills/commit-and-pr/SKILL.md
---
name: ship
description: テスト→コミット→PR作成を一括実行
user-invocable: true
---

以下の手順を実行してください:
1. pnpm lint && pnpm test を実行
2. すべて通ったら変更をコミット（Conventional Commits 形式）
3. リモートにプッシュ
4. gh で PR を作成（変更内容のサマリーを本文に含める）
```

使い方: `/ship`

---

## 12. よくある失敗と対策

### 失敗 1: コンテキストの肥大化

**症状**: Claude の応答が遅くなる、的外れな回答が増える
**対策**: `/clear` をタスク間で必ず使う。1セッション = 1タスクを徹底。

### 失敗 2: 曖昧なプロンプト

**症状**: 期待と異なるコードが生成される
**対策**: 具体的なファイルパス、既存パターンへの参照、テストケースを含める。

### 失敗 3: 一度に大量の変更を依頼

**症状**: 変更が中途半端になる、整合性が崩れる
**対策**: 1プロンプト = 1つの明確なタスク。大きな機能は分割して段階的に進める。

### 失敗 4: Plan Mode を使わない

**症状**: 既存コードと整合しない実装が生まれる
**対策**: 新機能や修正の前に必ず Plan Mode で調査・計画する。

### 失敗 5: テストなしでコミット

**症状**: リグレッションが発生する
**対策**: CLAUDE.md に「テストを実行して通ることを確認してからコミット」と明記する。

### 失敗 6: 同じミスを 2 回以上修正させる

**症状**: 何度も同じ方向性で修正を試みて失敗する
**対策**: `/clear` してアプローチを変えた新しいプロンプトで再開する。

---

## クイックリファレンス

```
開発開始時:
  claude /init          → CLAUDE.md 生成
  claude /permissions   → パーミッション設定
  claude /hooks         → Hooks 設定

日常開発:
  Shift+Tab            → Plan / Normal Mode 切替
  /clear               → タスク間でコンテキストリセット
  /compact             → コンテキスト手動圧縮
  /context             → 使用量確認
  Esc                  → 中断
  Ctrl+G               → エディタで入力編集
  Option+T             → Extended Thinking 切替

セッション管理:
  claude --continue     → 前回セッション再開
  claude --resume       → セッション一覧から選択
  /rename <name>       → セッションに名前付け
  claude --worktree <n> → 並列作業用 Worktree
```
