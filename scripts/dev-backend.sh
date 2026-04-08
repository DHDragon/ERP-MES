#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

if [ ! -f .env.local ]; then
  ./scripts/select-ports.sh
fi
set -a
source ./.env.local
set +a

cd backend
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=${MES_BACKEND_PORT} --spring.datasource.url=jdbc:mysql://localhost:${MES_MYSQL_PORT}/erp_dev?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai --spring.datasource.username=root --spring.datasource.password=root --spring.data.redis.host=localhost --spring.data.redis.port=${MES_REDIS_PORT}"
