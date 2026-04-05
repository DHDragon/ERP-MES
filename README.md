# ERP-MES

第一包：项目基础骨架 + 通用能力层

## 目录
- `frontend/` Vue3 + TS + Vite + Element Plus + Pinia
- `backend/` Spring Boot 3 + Java 17 + Security(JWT) + MyBatis-Plus + Redis
- `db/` 数据库脚本（如需额外独立脚本）

## 启动步骤（本地开发）

### 1. 准备依赖
- Node.js 18+
- JDK 17
- MySQL 8
- Redis（可选，当前骨架已集成但不强依赖业务）

### 2. 初始化数据库
创建数据库：
```sql
CREATE DATABASE erp_dev DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

执行 Flyway 脚本（随应用启动自动执行），或手工执行：
- `backend/src/main/resources/db/migration/V1__init_base_tables.sql`
- `backend/src/main/resources/db/init/*.sql`

### 3. 启动后端
```bash
cd backend
mvn -q -DskipTests spring-boot:run
```

健康检查：
- `GET http://localhost:8080/api/health`

登录（演示链路）：
- `POST http://localhost:8080/api/auth/login`
  - body: `{ "username": "admin", "password": "admin123" }`

### 4. 启动前端
```bash
cd frontend
npm i
npm run dev
```

访问：
- http://localhost:5173

## 说明
- 当前登录链路为“最小可跑通”演示：账号密码暂未接 DB（待第二步对齐《在线版》字段与表结构后接入）。
- 所有 Result 结构：`code/message/data/traceId`。
