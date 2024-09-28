import 'dart:convert';

import 'package:divkit/divkit.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

const laps = 1000;

class TestStatistics {
  final int hash;
  final int mergeUs;
  final int dtoBuildUs;

  const TestStatistics(
    this.hash,
    this.mergeUs,
    this.dtoBuildUs,
  );

  @override
  String toString() => "Merge took: ${mergeUs / 1000} ms\n"
      "DTO [$hash] build took: ${dtoBuildUs / 1000} ms\n";
}

Future<List<TestStatistics>> test() async {
  final tests = [
    'assets/perf/with_templates.json',
    'assets/perf/without_templates.json',
  ];

  final list = <TestStatistics>[];

  for (final name in tests) {
    final raw = jsonDecode(await rootBundle.loadString(name));

    int avgMergeUs = 0;
    int avgDtoBuildUs = 0;
    int hash = -1;

    for (int i = 0; i < laps; ++i) {
      final stopwatch = Stopwatch()..start();
      final json = await TemplatesResolver(
        layout: raw['card']!,
        templates: raw['templates'],
      ).merge();
      stopwatch.stop();

      avgMergeUs += stopwatch.elapsed.inMicroseconds;

      stopwatch
        ..reset()
        ..start();
      final data = DivData.fromJson(json);
      stopwatch.stop();

      avgDtoBuildUs += stopwatch.elapsed.inMicroseconds;

      hash = data.hashCode;
    }

    list.add(TestStatistics(hash, avgMergeUs ~/ laps, avgDtoBuildUs ~/ laps));
  }

  return list;
}

void main() async {
  /// Running together with the Flutter engine,
  /// for at least some approximation to real use.
  runApp(const TestRunner());
}

class TestRunner extends StatelessWidget {
  const TestRunner({super.key});

  @override
  Widget build(BuildContext context) {
    return Directionality(
      textDirection: TextDirection.ltr,
      child: ColoredBox(
        color: Colors.white,
        child: Center(
          child: Material(
            type: MaterialType.transparency,
            child: FutureBuilder<List<TestStatistics>>(
                future: test(),
                builder: (context, snapshot) {
                  if (snapshot.hasData) {
                    final message = "${snapshot.requireData[0].toString()}\n\n"
                        "${snapshot.requireData[1].toString()}";
                    if (kDebugMode) {
                      print(message);
                    }
                    return Text(message);
                  } else if (snapshot.hasError) {
                    return Text(snapshot.error.toString());
                  }

                  return const Text('PERF TEST RUNNING');
                }),
          ),
        ),
      ),
    );
  }
}
