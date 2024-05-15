import 'dart:async';

import 'framework/flutter_test_config.dart';

Future<void> testExecutable(
  FutureOr<void> Function() testMain,
) async =>
    goldenTestsTestExecutableForCI(testMain);
