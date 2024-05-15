#!/bin/bash
set -eu

fvm flutter pub get
fvm dart format -o none . --set-exit-if-changed
fvm flutter analyze --no-pub --no-congratulate --no-fatal-infos
