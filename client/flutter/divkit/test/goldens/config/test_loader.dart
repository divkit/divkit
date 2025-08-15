import 'dart:convert';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:path/path.dart' as path_package;

import '../models/snapshot_test_model.dart';
import '../models/test_files.dart';
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
      final id = entity.path
          .replaceAll('$testDataPathBaseDir/', '')
          .replaceAll('.json', '');

      try {
        final rawData = jsonDecode(await entity.readAsString());
        final testCase = SnapshotTestModel.fromJson(rawData);

        if (!(testCase.availablePlatforms?.contains('flutter') ?? true)) {
          print('Skipping test: $id');
        } else {
          testCases.add(
            GoldenTestCase(
              path_package.withoutExtension(id),
              testCase,
            ),
          );
        }
      } catch (e) {
        print('Failed loading $id: $e');
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
