import 'dart:convert';
import 'dart:io';

import 'models/test_group.dart';

Future<Map<String, TestGroup>> loadTestData(String path) async {
  Map<String, TestGroup> testCases = {};
  final List<FileSystemEntity> entities;

  if (path.endsWith('.json')) {
    entities = [File(path)];
  } else {
    final dir = Directory(path);
    entities = await dir.list().toList();
  }

  for (var entity in entities) {
    final path = entity.path;
    if (entity is File && path.endsWith('.json')) {
      final testCaseJson = await jsonDecode(
        await entity.readAsString(),
      );
      final name = path.split('/').last.split('.').first;
      testCases[name] = TestGroup.fromJson(testCaseJson);
    }
  }

  return testCases;
}
