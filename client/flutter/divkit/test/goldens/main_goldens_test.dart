import 'package:collection/collection.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_test/flutter_test.dart';

import 'models/test_files.dart';
import 'config/skipped_tests.dart';
import 'config/test_data_paths.dart';
import 'config/test_loader.dart';
import 'config/test_runner.dart';

void main() async {
  for (final testDataPath in testDataPaths) {
    final tests = await loadTestData(testDataPath);

    final totalTestsLength = [
      ...tests.whereType<GoldenTestGroup>().map((e) => e.testCases).flattened,
      ...tests.whereType<GoldenTestCase>(),
    ].length;

    if (kDebugMode) {
      print('Running tests...');
      print('Will skip ${skippedTests.length}/$totalTestsLength tests');
    }
    await runTests(tests);
  }
}
