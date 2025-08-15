#!/bin/bash
set -eu

# Default values
PLATFORM=""
BUILD_NUMBER=""
INPUT="example"

if [ $# -le 1 ]; then
  echo "No parameters provided."
  exit 1
fi

while [ -n "${1-}" ]; do
    case $1 in
        -p) PLATFORM=$2; shift 2;;
        -n) BUILD_NUMBER=$2;  shift 2;;
        -i) INPUT=$2;  shift 2;;
        *) echo "Unknown parameter passed: $1"; exit 1 ;;
    esac
done

if [ -n "$INPUT" ]; then
  cd "$INPUT"
fi

fvm flutter pub get

case $PLATFORM in
    ios)
        pushd ios
        pod install --repo-update
        popd

        fvm flutter build ios --release \
            --verbose \
            --build-number="$BUILD_NUMBER" \
            --no-codesign
    ;;
    android)
        fvm flutter build apk --release \
            --verbose \
            --build-number="$BUILD_NUMBER"
    ;;
    *)
        echo "Invalid target platform: $PLATFORM" >&2
        exit 1
    ;;
esac
