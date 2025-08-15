#!/bin/bash

set -eu

fvm flutter test test/unit
fvm flutter test test/expression_runtime || true
