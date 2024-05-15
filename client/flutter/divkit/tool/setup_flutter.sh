#!/bin/bash
set -eu

VERSION=""
FVM_HOST=""

if [ $# -le 1 ]; then
  echo "No parameters provided."
  exit 1
fi

while [ -n "${1-}" ]; do
    case $1 in
        -v) VERSION=$2; shift 2;;
        -h) FVM_HOST=$2;  shift 2;;
        *) echo "Unknown parameter passed: $1"; exit 1 ;;
    esac
done

if [ -n "$FVM_HOST" ]; then
  export FVM_GIT_CACHE="$FVM_HOST"
fi

if [ -n "$VERSION" ]; then
  fvm install --verbose "$VERSION"
  fvm use --verbose "$VERSION" --force
  fvm flutter precache --verbose

  echo "Flutter successfully setup!"
else
  echo "Flutter version not selected!"
fi
