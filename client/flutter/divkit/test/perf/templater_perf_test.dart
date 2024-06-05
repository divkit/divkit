import 'package:divkit/divkit.dart';
import 'dart:convert';
import 'dart:io';

void main() async {
  final tests = [
    // Merge took: 43 ms
    // DTO build took: 62 ms
    'example/assets/test_data/perf_test_data/with_templates',
    // Merge took: 0 ms
    // DTO build took: 23 ms
    'example/assets/test_data/perf_test_data/without_templates'
  ];

  for (final name in tests) {
    final input = File('$name.json');
    final raw = jsonDecode(await input.readAsString());

    final stopwatch = Stopwatch()..start();
    final json = TemplatesResolver(
      layout: raw['card']!,
      templates: raw['templates'],
    ).merge();
    stopwatch.stop();
    print("Merge took: ${stopwatch.elapsed.inMilliseconds} ms");

    stopwatch
      ..reset()
      ..start();
    final data = DivData.fromJson(json);
    stopwatch.stop();
    print("DTO build took: ${stopwatch.elapsed.inMilliseconds} ms");
  }
}
