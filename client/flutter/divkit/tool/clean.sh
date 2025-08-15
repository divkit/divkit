#!/bin/bash
set -eu

# Define the target path for the symlink
target_path="test_data"

# Code to delete the symlink safely
if [[ -L "$target_path" ]]; then
    # If the target is indeed a symlink, safely unlink it
    unlink "example/assets/samples"
    unlink "example/assets/regression"
    unlink "example/assets/perf"
    unlink "test_data"

    echo "Symlink deleted successfully."
else
    echo "No symlink found at target path. Skip deletion."
fi
fvm flutter clean
