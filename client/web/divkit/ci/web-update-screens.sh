#!/bin/bash

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
npm run build:prod
npm run test:hermione:update

arc co -b update-screens-${TARGET_REVISION_HASH}
cd tests && arc add . && cd -
arc status .
arc commit -m "DIVKIT-0: Update screens"
arc push
arc log . -n 1 --oneline
