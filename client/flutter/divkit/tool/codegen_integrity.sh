#!/bin/bash
set -eu

# Default values
DIFF_CHECKER=""

while [ -n "${1-}" ]; do
    case $1 in
        -c) DIFF_CHECKER=$2; shift 2;;
        *) echo "Unknown parameter passed: $1"; exit 1 ;;
    esac
done

./tool/generate_scheme.sh

echo "Validating codegen..."
VCS_DIFF=$(eval "$DIFF_CHECKER")
if [[ -z "${VCS_DIFF// }" ]]; then
    echo "Validation done."
else
    echo "Generated files haven't been committed!"
    echo "Difference: $VCS_DIFF"
    exit 1
fi
