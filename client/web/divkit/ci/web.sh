#!/bin/bash

set -ex

echo "node `node -v`"
echo "npm `npm -v`"

# setup private ssh key
echo "$ROBOT_SSH_KEY" > id_rsa
chmod 400 id_rsa
ssh-add id_rsa

export PYTHONPATH=/home/zomb-sandbox/client:$PYTHONPATH

export checkout_config="{
    \"commit\": \"$TARGET_REVISION_HASH\",
    \"type\": \"arc\",
    \"mount_path\": \"$RESULT_RESOURCES_PATH/arc_mount\"
  }"

npm ci
cd ci && npm ci && cd -
node ci/checks.js
