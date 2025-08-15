#!/bin/bash

scriptDir=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
cd $scriptDir

config=$1
schemaDir=$2
outputDir=$3

optionalArgs=""
if [ "$#" -eq 4 ]; then
  optionalArgs=$4
fi

if [ "$#" -eq 5 ]; then
  optionalArgs="$4 $5"
fi

echo Executing api_generator with [config = $config] [schemaDir = $schemaDir] [outputDir = $outputDir] [optionalArgs = $optionalArgs]
python3 -m api_generator -c $config -s $schemaDir -o $outputDir $optionalArgs
