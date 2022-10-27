#!/bin/bash

scriptDir=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

config=$scriptDir/codegen_config.json
schemaDir=$scriptDir/../../schema
outputDir=$scriptDir/pydivkit/div

apiGenDir=$scriptDir/../../api_generator

cd $apiGenDir
echo Executing api_generator with [config = $config] [schemaDir = $schemaDir] [outputDir = $outputDir]
python3 -m api_generator -c $config -s $schemaDir -o $outputDir

echo "Install poetry"
python3 -m pip install poetry

echo "Install requirements"
(cd $outputDir/../.. && python3 -m poetry install --no-root -n)
echo "Reformat code"
(cd $outputDir/../.. && python3 -m poetry run gray $outputDir)
