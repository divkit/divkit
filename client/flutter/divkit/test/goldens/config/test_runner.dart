import 'package:divkit/divkit.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:path/path.dart' as path_package;

import 'test_maker.dart';
import '../models/test_files.dart';

Future<void> runTests(List<GoldenTestFile> testEntries) async {
  for (var testEntry in testEntries) {
    if (testEntry is GoldenTestCase) {
      final testCaseName = path_package.basename(testEntry.name);
      final testFileName = testEntry.name.replaceAll('/', '.');

      makeGoldenTest(
        description: testCaseName,
        fileName: testFileName,
        builder: () => DivKitView(
          textDirection: TextDirection.ltr,
          data: DefaultDivKitData.fromScheme(
            card: testEntry.testCase.testData,
            templates: testEntry.testCase.testTemplates,
          ),
        ),
      );
    } else if (testEntry is GoldenTestGroup) {
      group(testEntry.name, () {
        runTests(testEntry.testCases);
      });
    }
  }
}
