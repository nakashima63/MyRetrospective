# 技術スタック選定 — MyRetrospective

> 3ヶ月でリリース可能な構成を、学習効果とのバランスを考慮して選定する。
> Docker で完全セルフホスト。ラズパイ・レンタルサーバー・VPS のどこにでもデプロイ可能。

---

## アーキテクチャ概要

```
                        docker compose up -d
┌─────────────────────────────────────────────────────────────────┐
│  Docker Compose                                                 │
│                                                                 │
│  ┌───────────────┐  proxy   ┌───────────────┐                  │
│  │   nginx       │ ───────▶ │  Spring Boot  │                  │
│  │   (Frontend)  │  /api/*  │  (Backend)    │                  │
│  │               │          │               │                  │
│  │  React SPA    │          │  REST API     │                  │
│  │  静的ファイル配信│          │  認証 (JWT)   │                  │
│  │  :80          │          │  :8080        │                  │
│  └───────────────┘          └───────┬───────┘                  │
│                                     │ JDBC                     │
│                              ┌──────┴────────┐                 │
│                              │  PostgreSQL   │                 │
│                              │  :5432        │                 │
│                              │               │                 │
│                              │  Volume:      │                 │
│                              │  pgdata       │                 │
│                              └───────────────┘                 │
└─────────────────────────────────────────────────────────────────┘
```

**ポイント:**
- `docker compose up -d` だけで全環境が起動する
- nginx がリバースプロキシを兼ねる（`/api/*` → Spring Boot、それ以外 → React SPA）
- PostgreSQL のデータは Docker Volume で永続化
- ラズパイ (ARM64)・レンタルサーバー・VPS のどこでも同一構成で動作

---

## 各技術の選定理由

### Frontend

| 技術 | バージョン | 選定理由 |
|---|---|---|
| **React** | 19 | コンポーネントベースUI。エコシステムが最大。学習リソースが豊富 |
| **React Router v7 (Data Mode)** | 7.x | Spring Boot と組み合わせる SPA に最適（後述） |
| **TypeScript** | 5.x | 型安全性でバグを防ぐ。リファクタリングの安心感 |
| **Vite** | 6.x | 高速な HMR。React + TS のデファクト標準ビルドツール |
| **Tailwind CSS** | 4.x | ユーティリティファーストCSS。プロトタイピングが速い |
| **shadcn/ui** | latest | コピペ型UIコンポーネント。カスタマイズ自由度が高い |

### React Router v7: なぜ Data Mode か

React Router v7 には 3 つのモードがある。

| モード | 特徴 | 本プロジェクトとの適合性 |
|---|---|---|
| **Declarative** | 基本的なURL→コンポーネントのマッピングのみ | △ データ取得の仕組みがない |
| **Data Mode** | ルートごとの loader/action でデータ取得・更新を統合 | **◎ SPA + 外部API 構成に最適** |
| **Framework Mode** | Remix ベースのフルスタックフレームワーク | × バックエンドが Spring Boot なので過剰 |

**Data Mode を選ぶ理由:**
- `loader` で Spring Boot API からデータを取得し、ルートに紐付けて管理できる
- SPA としてビルド → nginx で静的配信するだけ
- Framework Mode のようなサーバーランタイム不要
- 学習コストが適度（Declarative より実践的、Framework Mode より軽量）

```typescript
// Data Mode のイメージ
const router = createBrowserRouter([
  {
    path: "/retrospectives/:id",
    loader: async ({ params }) => {
      return fetch(`/api/retrospectives/${params.id}`, {
        headers: { Authorization: `Bearer ${getToken()}` }
      });
    },
    Component: RetrospectiveDetail,
  },
]);
```

### Backend

| 技術 | バージョン | 選定理由 |
|---|---|---|
| **Spring Boot** | 3.4.x | Java の Web フレームワーク標準。学習目的にも最適 |
| **Java** | 21 (LTS) | 最新 LTS。record、sealed class、パターンマッチングが使える |
| **Spring Data JPA** | - | DB アクセスの定型コードを削減 |
| **Spring Security** | - | JWT 認証・認可の実装 |
| **Flyway** | - | DBマイグレーション管理 |
| **Gradle** | 8.x | ビルドツール。Kotlin DSL で記述 |

### データベース・認証

| 技術 | 選定理由 |
|---|---|
| **PostgreSQL 16** | Docker で起動。信頼性が高く、JSON型やFull Text Searchも使える |
| **Spring Security + JWT** | 自前で認証を実装。学習効果が高い。外部依存なし |
| **jjwt (io.jsonwebtoken)** | JWT の生成・検証ライブラリ。Spring Security と統合 |
| **BCrypt** | パスワードハッシュ化。Spring Security 内蔵 |

### テスト

| 技術 | 対象 | 用途 |
|---|---|---|
| **Vitest** | Frontend | コンポーネント・ユーティリティの単体テスト |
| **Testing Library** | Frontend | ユーザー視点の UI テスト |
| **Playwright** | Frontend | E2E テスト（主要フロー） |
| **JUnit 5** | Backend | Spring Boot の単体・統合テスト |
| **MockMvc** | Backend | API エンドポイントのテスト |
| **Testcontainers** | Backend | テスト用 PostgreSQL を Docker で自動起動 |

### インフラ・デプロイ

| 技術 | 用途 |
|---|---|
| **Docker** | 各サービスのコンテナ化 |
| **Docker Compose** | マルチコンテナのオーケストレーション |
| **nginx** | リバースプロキシ + SPA 静的配信 |
| **GitHub Actions** | CI（テスト・ビルド・イメージ作成） |

---

## Docker 構成

### docker-compose.yml の構成

```yaml
services:
  # --- Frontend (nginx + React SPA) ---
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    ports:
      - "80:80"
    depends_on:
      - backend

  # --- Backend (Spring Boot) ---
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/myretro
      SPRING_DATASOURCE_USERNAME: myretro
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
    depends_on:
      db:
        condition: service_healthy

  # --- Database ---
  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: myretro
      POSTGRES_USER: myretro
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - pgdata:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U myretro"]
      interval: 5s
      timeout: 5s
      retries: 5

volumes:
  pgdata:
```

### Dockerfile 概要

**Frontend (マルチステージビルド):**
```dockerfile
# Build stage
FROM node:22-alpine AS build
WORKDIR /app
COPY package.json pnpm-lock.yaml ./
RUN corepack enable && pnpm install --frozen-lockfile
COPY . .
RUN pnpm build

# Production stage
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
```

**Backend (マルチステージビルド):**
```dockerfile
# Build stage
FROM gradle:8-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon

# Production stage
FROM eclipse-temurin:21-jre-alpine
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### nginx.conf (リバースプロキシ)

```nginx
server {
    listen 80;

    # API リクエストは Spring Boot へ転送
    location /api/ {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # それ以外は React SPA の静的ファイルを返す
    location / {
        root /usr/share/nginx/html;
        index index.html;
        try_files $uri $uri/ /index.html;  # SPA のクライアントサイドルーティング対応
    }
}
```

### 環境変数管理

```bash
# .env（git 管理外）
DB_PASSWORD=your_secure_password
JWT_SECRET=your_jwt_secret_key_at_least_256_bits
```

### 開発時 vs 本番

| | 開発時 | 本番 |
|---|---|---|
| Frontend | `pnpm dev` (Vite, HMR) | nginx コンテナで静的配信 |
| Backend | IDE から直接起動 or `./gradlew bootRun` | Docker コンテナ |
| DB | `docker compose up db` (DB だけ起動) | Docker コンテナ |
| 起動コマンド | 各サービス個別起動 | `docker compose up -d` |

開発時は DB だけ Docker で起動し、Frontend/Backend はローカルで起動するのが効率的。

```bash
# 開発用 compose (DB のみ)
docker compose -f docker-compose.dev.yml up -d
```

---

## プロジェクト構成

```
MyRetrospective/
├── frontend/                    # React SPA
│   ├── src/
│   │   ├── routes/              # ページコンポーネント（React Router）
│   │   ├── components/          # 再利用可能 UI コンポーネント
│   │   │   └── ui/              # shadcn/ui コンポーネント
│   │   ├── lib/                 # ユーティリティ・API クライアント
│   │   ├── hooks/               # カスタム React Hooks
│   │   ├── types/               # TypeScript 型定義
│   │   └── main.tsx             # エントリポイント
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   ├── vite.config.ts
│   ├── tsconfig.json
│   └── tailwind.config.ts
│
├── backend/                     # Spring Boot API
│   ├── src/main/java/com/myretro/
│   │   ├── config/              # SecurityConfig, JwtConfig 等
│   │   ├── controller/          # REST Controller
│   │   ├── service/             # ビジネスロジック
│   │   ├── repository/          # JPA Repository
│   │   ├── entity/              # JPA Entity
│   │   ├── dto/                 # Request/Response DTO
│   │   ├── security/            # JWT フィルター・プロバイダー
│   │   └── exception/           # 例外ハンドリング
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   ├── application-dev.yml  # 開発用プロファイル
│   │   └── db/migration/        # Flyway マイグレーション
│   ├── src/test/java/com/myretro/
│   ├── Dockerfile
│   └── build.gradle.kts
│
├── docker-compose.yml           # 本番用（全サービス）
├── docker-compose.dev.yml       # 開発用（DB のみ）
├── .env.example                 # 環境変数テンプレート
├── .env                         # 環境変数（git 管理外）
│
├── docs/                        # ドキュメント
│   ├── claude-code-guide.md
│   └── tech-stack.md            # ← このファイル
│
├── CLAUDE.md                    # Claude Code 設定
├── .gitignore
└── .github/
    └── workflows/               # CI（テスト・ビルド）
```

---

## 認証フロー（セルフホスト JWT）

```
Frontend (React)                          Spring Boot
   │                                          │
   │ 1. POST /api/auth/signup                 │
   │    { email, password }                   │
   │ ────────────────────────────────────────▶ │
   │                                          │ BCrypt でパスワードハッシュ化
   │                                          │ users テーブルに保存
   │    { message: "Created" }                │
   │ ◀──────────────────────────────────────── │
   │                                          │
   │ 2. POST /api/auth/login                  │
   │    { email, password }                   │
   │ ────────────────────────────────────────▶ │
   │                                          │ パスワード検証
   │                                          │ JWT 生成 (jjwt)
   │    { accessToken, refreshToken }         │
   │ ◀──────────────────────────────────────── │
   │                                          │
   │ 3. GET /api/retrospectives               │
   │    Authorization: Bearer <accessToken>   │
   │ ────────────────────────────────────────▶ │
   │                                          │ JwtAuthenticationFilter
   │                                          │ トークン検証 → SecurityContext
   │    { data: [...] }                       │
   │ ◀──────────────────────────────────────── │
   │                                          │
   │ 4. POST /api/auth/refresh                │
   │    { refreshToken }                      │
   │ ────────────────────────────────────────▶ │
   │                                          │ リフレッシュトークン検証
   │    { accessToken (new) }                 │ 新しい accessToken 発行
   │ ◀──────────────────────────────────────── │
```

**Spring Security の構成:**
- `JwtAuthenticationFilter` — リクエストから Bearer トークンを取得・検証
- `SecurityConfig` — 認証不要パス（`/api/auth/**`）と認証必須パスを定義
- `UserDetailsService` — DB からユーザー情報を取得
- accessToken (短命: 15分) + refreshToken (長命: 7日) のペア運用

---

## 開発フェーズ（3ヶ月ロードマップ概案）

### Phase 1: 基盤構築（1〜2 週目）
- プロジェクトセットアップ（frontend / backend）
- Docker Compose 環境構築（PostgreSQL + 開発用 compose）
- DB スキーマ設計 + Flyway マイグレーション
- 認証フロー（サインアップ / ログイン / JWT）
- CI パイプライン構築（GitHub Actions）

### Phase 2: コア機能（3〜6 週目）
- KPT 振り返りの CRUD（API + UI）
- KPT 入力フォーム（カード形式）
- 振り返り一覧・詳細表示
- カテゴリ別の整理・並べ替え

### Phase 3: アクション化・分析（7〜9 週目）
- Try → アクションアイテムへの変換
- アクションのステータス管理（未着手/進行中/完了）
- 振り返り履歴のタイムライン表示
- ダッシュボード（傾向の可視化）

### Phase 4: 仕上げ・リリース（10〜12 週目）
- UI/UX の磨き込み
- レスポンシブ対応
- 本番 Docker イメージ最適化
- E2E テスト追加
- 本番デプロイ（ラズパイ or レンタルサーバー）

**MVP ライン**: Phase 2 完了時点でリリース可能な状態を目指す。

---

## 主要ライブラリ一覧

### Frontend (package.json)

| パッケージ | 用途 |
|---|---|
| `react`, `react-dom` | UI フレームワーク |
| `react-router` | ルーティング (Data Mode) |
| `tailwindcss` | CSS フレームワーク |
| `class-variance-authority` | shadcn/ui の基盤 |
| `clsx`, `tailwind-merge` | クラス名ユーティリティ |
| `lucide-react` | アイコン |
| `@dnd-kit/core` | ドラッグ&ドロップ（KPTカード並べ替え） |
| `zod` | バリデーション |
| `date-fns` | 日付操作 |

### Backend (build.gradle.kts)

| 依存関係 | 用途 |
|---|---|
| `spring-boot-starter-web` | REST API |
| `spring-boot-starter-data-jpa` | DB アクセス |
| `spring-boot-starter-security` | 認証・認可 |
| `spring-boot-starter-validation` | 入力バリデーション |
| `postgresql` | PostgreSQL ドライバ |
| `io.jsonwebtoken:jjwt-api/impl/jackson` | JWT 生成・検証 |
| `flyway-core` | DB マイグレーション |
| `springdoc-openapi` | API ドキュメント自動生成 |
| `lombok` | ボイラープレート削減 |

---

## デプロイ手順

### ラズパイ / レンタルサーバー共通

```bash
# 1. リポジトリを clone
git clone https://github.com/your-name/MyRetrospective.git
cd MyRetrospective

# 2. 環境変数を設定
cp .env.example .env
vim .env  # DB_PASSWORD, JWT_SECRET を設定

# 3. 起動
docker compose up -d

# 4. 確認
curl http://localhost/api/health
```

### ラズパイ固有の注意点

- `postgres:16-alpine` は ARM64 対応済み
- Spring Boot の Docker イメージも `eclipse-temurin:21-jre-alpine` が ARM64 対応
- メモリ制限がある場合は compose で `mem_limit` を設定

```yaml
# docker-compose.yml に追加
services:
  backend:
    mem_limit: 512m
    environment:
      JAVA_OPTS: "-Xmx384m"
```

---

## リスクと対策

| リスク | 影響 | 対策 |
|---|---|---|
| JWT 認証の自前実装ミス | セキュリティ脆弱性 | Spring Security のベストプラクティスに従う。テスト必須 |
| Docker ビルド時間（特にラズパイ） | 開発効率低下 | マルチステージビルドでイメージ軽量化。CI でビルド済みイメージを作成 |
| Spring Boot の学習コスト | 開発速度低下 | Claude Code を活用して学びながら実装 |
| 3ヶ月でスコープ超過 | リリースできない | Phase 2 までを MVP とし、Phase 3-4 は優先度で取捨選択 |
| ラズパイのリソース制限 | パフォーマンス問題 | JVM メモリ上限の設定、PostgreSQL のチューニング |
| CORS 設定の問題 | 開発初期で詰まる | nginx がプロキシするため本番では CORS 不要。開発時は Vite proxy で対応 |
