#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
cd "$SCRIPT_DIR"

# Ensure output directory exists
mkdir -p src/generated

PYTHONPATH="$REPO_ROOT/api_generator" uv run --no-project \
    python -m api_generator \
    -c codegen_config.json \
    -s "$REPO_ROOT/schema" \
    -o src/generated

# Format generated code to match rustfmt expectations
if command -v cargo >/dev/null 2>&1; then
    cargo fmt -- src/generated/*.rs
fi

echo "Rust code generation complete."
echo "Generated files in src/generated/"
