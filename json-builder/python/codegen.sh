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

echo "Install poetry"
(cd $scriptDir && python3 -m venv env && env/bin/pip install poetry)

echo "Install requirements"
(cd $scriptDir && env/bin/poetry install --no-root -n)

echo "Reformat code"
(cd $scriptDir && env/bin/poetry run gray $outputDir)
