#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

# Ensure output directory exists
mkdir -p src/generated

python3 -m api_generator \
    -c codegen_config.json \
    -s ../../schema \
    -o src/generated

echo "Rust code generation complete."
echo "Generated files in src/generated/"
