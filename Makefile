include .env
export

.PHONY: dev dev-db dev-backend dev-frontend stop

# 開発環境を一括起動（DB → バックエンド → フロントエンド）
dev: dev-db dev-backend dev-frontend

# PostgreSQL のみ起動
dev-db:
	docker compose -f docker-compose.dev.yml up -d
	@echo "Waiting for DB to be ready..."
	@until docker exec myretro-db pg_isready -U myretro > /dev/null 2>&1; do sleep 1; done
	@echo "DB is ready."

# Spring Boot をバックグラウンドで起動
dev-backend:
	@echo "Starting backend..."
	@cd backend && SPRING_DATASOURCE_PASSWORD=$(DB_PASSWORD) ./gradlew bootRun > /tmp/myretro-backend.log 2>&1 &
	@echo "Waiting for backend to be ready..."
	@until curl -so /dev/null http://localhost:8080/api/auth/signup 2>/dev/null; do sleep 2; done
	@echo "Backend is ready. Logs: /tmp/myretro-backend.log"

# Vite 開発サーバーをフォアグラウンドで起動
dev-frontend:
	cd frontend && pnpm dev

# 開発環境を停止
stop:
	-@pkill -f "gradlew bootRun" 2>/dev/null || true
	-@pkill -f "spring-boot" 2>/dev/null || true
	docker compose -f docker-compose.dev.yml down
	@echo "All services stopped."
