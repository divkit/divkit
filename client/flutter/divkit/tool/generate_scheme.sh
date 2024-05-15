#!/bin/bash

# Generate phase
cd ../../../api_generator/ || exit
python3 -m api_generator -c ../client/flutter/divkit/generator_config.json -s ../schema -o ../client/flutter/divkit/lib/src/generated_sources

# Formatting phase
cd ../client/flutter/divkit || exit
fvm dart fix --apply .
fvm dart format .
