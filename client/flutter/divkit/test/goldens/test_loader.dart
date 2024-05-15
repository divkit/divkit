import 'dart:convert';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:path/path.dart' as path_package;

import 'models/snapshot_test_model.dart';
import 'models/test_files.dart';
import 'skipped_tests.dart';
import 'test_data_paths.dart';

Future<List<GoldenTestFile>> loadTestData(String path) async {
  final List<GoldenTestFile> testCases = [];
  final List<FileSystemEntity> entities;

  if (path.endsWith('.json')) {
    entities = [File(path)];
  } else {
    final dir = Directory(path);
    entities = await dir.list().toList();
  }

  for (var entity in entities) {
    if (entity is File && entity.path.endsWith('.json')) {
      if (skippedTestsMap.containsKey(entity.path)) {
        if (kDebugMode) {
          print('Skipping test for path ${entity.path}');
        }
      } else {
        final testCaseData = jsonDecode(await entity.readAsString());
        testCases.add(
          GoldenTestCase(
            path_package.withoutExtension(
              entity.path.replaceAll('$testDataPathBaseDir/', ''),
            ),
            SnapshotTestModel.fromJson(testCaseData),
          ),
        );
      }
    } else if (entity is Directory) {
      testCases.add(
        GoldenTestGroup(
          path_package.basenameWithoutExtension(entity.path),
          await loadTestData(entity.path),
        ),
      );
    }
  }

  return testCases;
}
