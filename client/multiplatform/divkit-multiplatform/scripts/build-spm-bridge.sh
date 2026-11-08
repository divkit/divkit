#!/bin/bash

set -euo pipefail

SDK="$1"
ARCH="$2"
SPM_BUILD_DIR="$3"
PACKAGE_DIR="$(cd "$(dirname "$0")/../../../ios" && pwd)"
SOURCE_PACKAGES_DIR="${DIVKIT_SPM_SOURCE_PACKAGES_DIR:-$PACKAGE_DIR/../multiplatform/build/spm-source-packages}"
mkdir -p "$SOURCE_PACKAGES_DIR"
SOURCE_PACKAGES_DIR="$(cd "$SOURCE_PACKAGES_DIR" && pwd)"

case "$SDK/$ARCH" in
    iphoneos/arm64) DESTINATION='generic/platform=iOS' ;;
    iphonesimulator/arm64|iphonesimulator/x86_64) DESTINATION='generic/platform=iOS Simulator' ;;
    *) echo "Unsupported iOS SDK/architecture: $SDK/$ARCH" >&2; exit 1 ;;
esac

cd "$PACKAGE_DIR"
# Gradle can run inside an Xcode build phase. Do not pass the app's exported
# build settings (linker flags, product paths, etc.) to this independent package.
XCODE_ENV=(
    "HOME=$HOME"
    "PATH=$PATH"
    "TMPDIR=${TMPDIR:-/tmp}"
    "DEVELOPER_DIR=${DEVELOPER_DIR:-$(xcode-select -p)}"
)
if [[ -n "${TOOLCHAINS:-}" ]]; then
    XCODE_ENV+=("TOOLCHAINS=$TOOLCHAINS")
fi
env -i "${XCODE_ENV[@]}" xcodebuild build \
    -scheme DivKitKMP \
    -configuration Debug \
    -sdk "$SDK" \
    -destination "$DESTINATION" \
    -derivedDataPath "$SPM_BUILD_DIR/DerivedData" \
    -clonedSourcePackagesDirPath "$SOURCE_PACKAGES_DIR" \
    -jobs 4 \
    -quiet \
    ARCHS="$ARCH" \
    ONLY_ACTIVE_ARCH=YES \
    CODE_SIGNING_ALLOWED=NO \
    COMPILER_INDEX_STORE_ENABLE=NO \
    SWIFT_OBJC_INTERFACE_HEADER_DIR="$SPM_BUILD_DIR/headers" \
    'SWIFT_OBJC_INTERFACE_HEADER_NAME=$(SWIFT_MODULE_NAME)-Swift.h' || {
    build_exit_code=$?
    echo "xcodebuild failed ($SDK/$ARCH): exit $build_exit_code" >&2
    exit "$build_exit_code"
}

HEADER="$SPM_BUILD_DIR/headers/DivKitKMP-Swift.h"
if [[ ! -s "$HEADER" ]]; then
    echo "Xcode did not generate the DivKitKMP Objective-C header: $HEADER" >&2
    exit 1
fi
