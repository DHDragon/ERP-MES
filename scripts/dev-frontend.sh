#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

if [ ! -f .env.local ]; then
  ./scripts/select-ports.sh
fi
set -a
source ./.env.local
set +a

cd frontend
npm run dev -- --host 0.0.0.0 --port ${MES_FRONTEND_PORT}
