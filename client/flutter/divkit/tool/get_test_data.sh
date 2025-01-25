#!/bin/bash
set -eu

pwd

# Define the source and target paths for the symlink
source_path="../../../test_data"
target_path="test_data"

example="example/assets"

# Check whether the symlink already exists
if [[ ! -L "$target_path" ]]; then
    # Create the symlink if it doesn't exist
    ln -sf "${source_path}" "${target_path}"
    ln -sf "../../${target_path}/samples" "${example}/samples"
    ln -sf "../../${target_path}/regression_test_data" "${example}/regression"
    ln -sf "../../${target_path}/perf_test_data" "${example}/perf"
    echo "Symlink created successfully."
else
    echo "Symlink already exists. Skipping creation."
fi
