#!/bin/bash
set -eu

if command -v adb &> /dev/null; then
  android_devices=$(adb devices -l | awk 'NR>1 {print $1}')

  if [ -n "$android_devices" ]; then
      echo "Connected Android device:"
      pushd example
      fvm flutter test integration_test/expression_test.dart | true
      fvm flutter test integration_test/speed_test.dart | true
      popd
  else
      echo "No Android devices found."
  fi
else
    echo "Android command line tools not found."
fi

if command -v xcrun &> /dev/null; then
    ios_devices=$(xcrun simctl list devices | grep -v "Shutdown" | awk '/^    /{print $1}')

    if [ -n "$ios_devices" ]; then
        echo "Connected iOS device:"
        pushd example
        fvm flutter test integration_test/expression_test.dart | true
        fvm flutter test integration_test/speed_test.dart | true
        popd
    else
        echo "No iOS devices found."
    fi
else
    echo "Xcode not found. iOS device detection is only available on macOS with Xcode."
fi

