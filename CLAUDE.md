# MyRetrospective

KPT（Keep / Problem / Try）を中心としたレトロスペクティブ支援アプリ。
入力 → 整理 → 次アクション化まで一気通貫で支援する。Docker で完全セルフホスト。

## 技術スタック

- **Frontend**: React 19 + React Router v7 (Data Mode) + TypeScript + Vite + Tailwind CSS 4 + shadcn/ui
- **Backend**: Spring Boot 3.4.x + Java 21 + Gradle (Kotlin DSL)
- **DB**: PostgreSQL 16（Flyway でマイグレーション管理）
- **認証**: Spring Security + JWT（自前実装）
- **インフラ**: Docker Compose（nginx + Spring Boot + PostgreSQL）

詳細は @docs/tech-stack.md を参照。

## コマンド

### Frontend (`frontend/`)

```bash
pnpm dev              # 開発サーバー起動
pnpm build            # プロダクションビルド
pnpm lint             # ESLint
pnpm format           # Prettier
pnpm format:check     # Prettier チェックのみ
```

### Backend (`backend/`)

```bash
./gradlew bootRun     # 開発サーバー起動
./gradlew build       # ビルド
./gradlew test        # テスト実行
```

## プロジェクト構成

```
MyRetrospective/
├── frontend/           # React SPA
├── backend/            # Spring Boot API
│   └── src/main/java/com/myretro/
│       ├── config/         # SecurityConfig 等
│       ├── controller/     # REST Controller
│       ├── service/        # ユースケース単位のサービス
│       ├── repository/     # JPA Repository
│       ├── entity/         # JPA Entity
│       ├── dto/            # Request/Response DTO
│       ├── security/       # JWT フィルター・プロバイダー
│       └── exception/      # 例外ハンドリング
├── docs/               # ドキュメント
└── CLAUDE.md
```

## 設計方針

### バックエンド

- **レイヤードアーキテクチャ**: Controller → Service → Repository のシンプルな構成
- **サービスはユースケース単位で作成する（単一責務）**
  - `CreateRetrospectiveService`, `ListRetrospectivesService` のように1クラス1ユースケース
  - リソース単位の肥大化したサービスクラス（例: `RetrospectiveService`）は作らない
- Java record を DTO に積極的に使用する
- エラーレスポンスは `ErrorResponse` record で統一（`GlobalExceptionHandler` で処理）

### フロントエンド

- 関数コンポーネント + hooks のみ
- ES Modules（import/export）、CommonJS は不可
- shadcn/ui コンポーネントをベースに UI を構築

## ブランチ・コミット規約

- ブランチ: `feature/#<issue番号>`, `fix/#<issue番号>`, `docs/*`
- コミットメッセージ: Conventional Commits 形式
  - `feat:` 新機能、`fix:` バグ修正、`docs:` ドキュメント、`refactor:` リファクタ、`chore:` 設定・雑務
- テストが通ることを確認してからコミットする

## テスト方針

- **Backend**: JUnit 5 + MockMvc。Controller のテストは `@SpringBootTest` + `@AutoConfigureMockMvc`
- **Frontend**: Vitest（予定）、E2E は Playwright（予定）
- テストプロファイルは `test`（H2 インメモリ DB を使用）
