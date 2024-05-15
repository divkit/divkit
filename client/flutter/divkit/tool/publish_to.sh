#!/bin/bash

# Read the publish_to value from release.yaml
publish_to=$1

# Check if the publish_to value is not empty
if [ -n "$publish_to" ]; then
  # Find all YAML files in the current directory and its subdirectories
  yaml_files=$(find . -type f -name "pubspec.yaml")

  # Iterate over each YAML file
  for file in $yaml_files; do
    # Replace all occurrences of the publish_to value in the YAML file
    sed -i.bak "s/publish_to:.*$/publish_to: \'$publish_to\'/" "$file"
  done

  echo "Replaced publish_to value in YAML files with: $publish_to"
else
  echo "Needs host argument"
fi
