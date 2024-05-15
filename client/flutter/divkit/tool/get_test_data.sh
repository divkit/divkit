#!/bin/bash
set -eu

pwd

# Define the source and target paths for the symlink
source_path="../../../../../test_data"
target_path="example/assets/test_data"

# Check whether the symlink already exists
if [[ ! -L "$target_path" ]]; then
    # Create the symlink if it doesn't exist
    ln -sf "${source_path}" "${target_path}"
    echo "Symlink created successfully."
else
    echo "Symlink already exists. Skipping creation."
fi
