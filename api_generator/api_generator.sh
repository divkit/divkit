#!/bin/bash

scriptDir=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
cd $scriptDir

config=$1
schemaDir=$2
outputDir=$3

echo Executing api_generator with [config = $config] [schemaDir = $schemaDir] [outputDir = $outputDir]
python3 -m api_generator -c $config -s $schemaDir -o $outputDir
