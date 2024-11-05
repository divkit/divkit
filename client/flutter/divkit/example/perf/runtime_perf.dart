import 'dart:convert';

import 'package:divkit/divkit.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

const laps = 1000;

class TestStatistics {
  final String expression;
  final int parseAndRunUs;
  final int parseOnceAndRunUs;
  final int optimoRunUs;
  final Object? res;

  const TestStatistics(
    this.expression,
    this.parseAndRunUs,
    this.parseOnceAndRunUs,
    this.optimoRunUs,
    this.res,
  );

  @override
  String toString() =>
      "Run ${expression.substring(0, 12)}... took: ${parseAndRunUs / 1000} ms | ${parseOnceAndRunUs / 1000} ms | ${optimoRunUs / 1000} ms";
}

Future<List<TestStatistics>> test() async {
  const name = 'assets/perf/expressions.json';

  final list = <TestStatistics>[];

  final raw = jsonDecode(await rootBundle.loadString(name));
  final expressions = raw['card']['states'][0]['div']['items'] as List;
  final variables = raw['card']['variables'] as List?;
  final Map<String, dynamic> context = variables != null
      ? {
          for (var entry in variables)
            entry['name']: safeParseNamed(entry['type'])(entry['value']),
        }
      : {};

  Object? res;

  for (final it in expressions) {
    int parseAndRunUs = 0;

    for (int i = 0; i < laps; ++i) {
      final stopwatch = Stopwatch()..start();

      try {
        res = runtime.execute(parser.parse(it['expression']).value, context);
      } catch (e) {
        res = e;
      }

      parseAndRunUs += stopwatch.elapsed.inMicroseconds;
    }

    int parseOnceAndRunUs = 0;
    final execTree = parser.parse(it['expression']).value;
    for (int i = 0; i < laps; ++i) {
      final stopwatch = Stopwatch()..start();

      try {
        res = runtime.execute(execTree, context);
      } catch (e) {
        res = e;
      }

      parseOnceAndRunUs += stopwatch.elapsed.inMicroseconds;
    }

    int optimoRunUs = 0;
    final expr = ResolvableExpression(it['expression']);
    final ctx = DivVariableContext(current: context);

    for (int i = 0; i < laps; ++i) {
      final stopwatch = Stopwatch()..start();

      try {
        res = expr.resolve(ctx);
      } catch (e) {
        res = e;
      }

      optimoRunUs += stopwatch.elapsed.inMicroseconds;
    }

    list.add(TestStatistics(
      it['expression'],
      parseAndRunUs ~/ laps,
      parseOnceAndRunUs ~/ laps,
      optimoRunUs ~/ laps,
      res,
    ));
  }

  return list;
}

void main() async {
  /// Running together with the Flutter engine,
  /// for at least some approximation to real use.
  runApp(const TestRunner());
}

class TestRunner extends StatefulWidget {
  const TestRunner({super.key});

  @override
  State<TestRunner> createState() => _TestRunnerState();
}

class _TestRunnerState extends State<TestRunner> {
  int runCount = 0;

  @override
  Widget build(BuildContext context) {
    return Directionality(
      textDirection: TextDirection.ltr,
      child: GestureDetector(
        onTap: () {
          setState(() {
            runCount++;
          });
        },
        child: ColoredBox(
          color: Colors.white,
          child: Center(
            child: Material(
              type: MaterialType.transparency,
              child: FutureBuilder<List<TestStatistics>>(
                  key: ValueKey(runCount),
                  future: test(),
                  builder: (context, snapshot) {
                    if (snapshot.hasData) {
                      final message = snapshot.requireData.join('\n');
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
      ),
    );
  }
}
