#!/bin/bash
set -eu

# Default values
NEEDS_UPDATE=false

while [ -n "${1-}" ]; do
    case $1 in
        -u | --update) NEEDS_UPDATE=true; shift 1;;
        *) echo "Unknown parameter passed: $1"; exit 1 ;;
    esac
done

if [ $NEEDS_UPDATE ]; then
  fvm flutter test test/goldens --update-goldens
else
  fvm flutter test test/goldens
fi

