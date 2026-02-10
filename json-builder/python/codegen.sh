#!/bin/bash

scriptDir=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

config=$scriptDir/codegen_config.json
schemaDir=$scriptDir/../../schema
outputDir=$scriptDir/pydivkit/div

apiGenDir=$scriptDir/../../api_generator

cd $apiGenDir
echo Executing api_generator with [config = $config] [schemaDir = $schemaDir] [outputDir = $outputDir]
python3 -m api_generator -c $config -s $schemaDir -o $outputDir

if [[ $1 = "--no-lint" ]]; then
  exit 0
fi

echo "Install dependencies"
(cd $scriptDir && uv sync --group dev --no-install-project)

echo "Check code"
(cd $scriptDir && uv run ruff check --fix $outputDir)

echo "Format code"
(cd $scriptDir && uv run ruff format $outputDir)
