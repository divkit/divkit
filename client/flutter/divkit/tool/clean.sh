#!/bin/bash
set -eu

# Define the target path for the symlink
target_path="example/assets/test_data"

# Code to delete the symlink safely
if [[ -L "$target_path" ]]; then
    # If the target is indeed a symlink, safely unlink it
    unlink $target_path
    echo "Symlink deleted successfully."
else
    echo "No symlink found at target path. Skip deletion."
fi

fvm flutter clean
