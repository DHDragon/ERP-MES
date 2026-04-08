#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

./scripts/select-ports.sh
set -a
source ./.env.local
set +a

docker compose up -d

echo ""
echo "Infra ready. Next run:"
echo "  ./scripts/dev-backend.sh"
echo "  ./scripts/dev-frontend.sh"
