#!/bin/bash
set -eu

# Define the target path for the symlink
target_path="test_data"

# Code to delete the symlink safely
if [[ -L "$target_path" ]]; then
    # If the target is indeed a symlink, safely unlink it
    unlink "test_data"
    unlink "samples"
    unlink "regression"
    unlink "perf_test_data"

    echo "Symlink deleted successfully."
else
    echo "No symlink found at target path. Skip deletion."
fi
fvm flutter clean
