#!/bin/bash

set -ex

echo "node `node -v`"
echo "npm `npm -v`"
python3 --version

export PYTHONPATH=/home/zomb-sandbox/client:$PYTHONPATH

export checkout_config="{
    \"commit\": \"$TARGET_REVISION_HASH\",
    \"type\": \"arc\",
    \"mount_path\": \"$RESULT_RESOURCES_PATH/arc_mount\"
  }"

npm ci
cd ci && npm ci && cd -
npm run gen
node ci/checks.js
