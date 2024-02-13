@echo off
setlocal enabledelayedexpansion

set "scriptDir=%~dp0"
cd /d "%scriptDir%"

set "config=%1"
set "schemaDir=%2"
set "outputDir=%3"

set "optionalArgs="
if "%4" neq "" (
  set "optionalArgs=!optionalArgs! %4"
)

if "%5" neq "" (
  set "optionalArgs=!optionalArgs! %5"
)

echo Executing api_generator with [config = !config!] [schemaDir = !schemaDir!] [outputDir = !outputDir!] [optionalArgs = !optionalArgs!]
python3 -u -m api_generator -c !config! -s !schemaDir! -o !outputDir! !optionalArgs!
