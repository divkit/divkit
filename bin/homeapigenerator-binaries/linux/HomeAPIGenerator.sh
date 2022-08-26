#!/bin/sh

EXECUTABLE="`dirname $0`/HomeAPIGenerator"
PATCHED_LD_PATH="$LD_LIBRARY_PATH:`dirname $0`"

checkldpath() {
  ERRORS=$(LC_ALL=C LD_LIBRARY_PATH="$1" ldd "${EXECUTABLE}" | grep 'not found')
  test -z "$ERRORS"
}

if checkldpath "$LD_LIBRARY_PATH"; then
  "$EXECUTABLE" $*
elif checkldpath "$PATCHED_LD_PATH"; then
  LD_LIBRARY_PATH="$PATCHED_LD_PATH" "$EXECUTABLE" $*
else
  # should fail, but try it anyway
  "$EXECUTABLE" $*
fi
