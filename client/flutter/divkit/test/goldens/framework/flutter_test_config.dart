import 'dart:async';

import 'package:golden_toolkit/golden_toolkit.dart';

/// Loads fonts for goldens.
/// Also enables real shadows.
///
/// Taken from https://pub.dev/packages/golden_toolkit#loading-fonts
///
/// Should be called inside `flutter_test_config.dart` like this
///
/// ```dart
/// Future<void> testExecutable(FutureOr<void> Function() testMain) async =>
///     goldenTestsTestExecutable(testMain);
/// ```
Future<void> goldenTestsTestExecutableForCI(
  FutureOr<void> Function() testMain,
) async =>
    GoldenToolkit.runWithConfiguration(
      () async {
        await loadAppFonts();
        return testMain();
      },
      config: GoldenToolkitConfiguration(
        enableRealShadows: true,
      ),
    );
