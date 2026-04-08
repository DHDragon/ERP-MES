#!/usr/bin/env bash
set -euo pipefail

pick_port() {
  for p in "$@"; do
    if ! nc -z 127.0.0.1 "$p" >/dev/null 2>&1; then
      echo "$p"
      return 0
    fi
  done
  return 1
}

MES_BACKEND_PORT=$(pick_port 18080 18081 18082 18083)
MES_FRONTEND_PORT=$(pick_port 5173 5174 5175 5176)
MES_MYSQL_PORT=$(pick_port 13306 13307 13308)
MES_REDIS_PORT=$(pick_port 16379 16380 16381)

cat > .env.local <<EOF
MES_BACKEND_PORT=${MES_BACKEND_PORT}
MES_FRONTEND_PORT=${MES_FRONTEND_PORT}
MES_MYSQL_PORT=${MES_MYSQL_PORT}
MES_REDIS_PORT=${MES_REDIS_PORT}
EOF

echo "Generated .env.local"
cat .env.local
