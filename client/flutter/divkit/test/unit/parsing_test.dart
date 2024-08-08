import 'package:flutter_test/flutter_test.dart';
import 'package:divkit/divkit.dart';
import 'dart:convert';
import 'dart:io';
import 'package:path/path.dart';

final directoryPath = 'example/assets/test_data/parsing_test_data';

Future<List<Map<String, dynamic>>> loadTests(String directoryPath) async {
  final directory = Directory(directoryPath);
  if (!await directory.exists()) {
    throw 'Directory does not exist';
  }

  final list = <Map<String, dynamic>>[];
  await for (var entity in directory.list(recursive: true)) {
    if (entity is File && extension(entity.path) == '.json') {
      final contents = await entity.readAsString();
      final data = jsonDecode(contents) as Map<String, dynamic>;
      data['path'] = entity.path;
      list.add(data);
    }
  }
  return list;
}

void main() async {
  final tests = await loadTests(directoryPath);

  group('Global DivKit parsing tests', () {
    for (final testCase in tests) {
      // We cannot pass this case due to the fact that the calculator sends strings on ios
      if (![
        'String value in boolean_int property (div-text.auto_ellipsize)',
        'Invalid item in array (transition_triggers) is ignored'
      ].contains(testCase['description'])) {
        test(
          testCase['description'] ?? testCase['path'],
          () {
            final data = DefaultDivKitData.fromJson(testCase).buildSync();
            final expected =
                DefaultDivKitData.fromJson(testCase['expected']).buildSync();
            expect(data, expected);
          },
        );
      }
    }
  });
}
