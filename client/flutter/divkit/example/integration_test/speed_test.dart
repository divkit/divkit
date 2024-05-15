import 'package:divkit/divkit.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';

Future<Duration> pluginSpeedTest({
  required DivExpressionResolver plugin,
  required String exp,
  required DivVariableContext context,
  required int iterationsCount,
}) async {
  Stopwatch stopwatch = Stopwatch()..start();
  for (int i = 0; i < iterationsCount; i++) {
    await plugin.resolve(
      ResolvableExpression(
        exp,
        parse: safeParseStr,
        fallback: null,
      ),
      context: context,
    );
  }
  return stopwatch.elapsed;
}

Future<void> main() async {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();
  group('Test calculations speed without variables', () {
    const exp = '@{2 + 2}';
    final context = DivVariableContext(current: const {});
    final plugin = NativeDivExpressionResolver();
    testWidgets(
      'Get calculations speed for 500 2+2 operations',
      (WidgetTester tester) async {
        const iterationsCount = 500;

        await plugin.clearVariables();
        for (int i = 0; i < 5; i++) {
          final res = await pluginSpeedTest(
            plugin: plugin,
            exp: exp,
            context: context,
            iterationsCount: iterationsCount,
          );
          print('$iterationsCount operations $exp in $res');
        }
      },
    );
    testWidgets(
      'Get calculations speed for 1000 2+2 operations',
      (WidgetTester tester) async {
        const iterationsCount = 1000;

        await plugin.clearVariables();
        for (int i = 0; i < 5; i++) {
          final res = await pluginSpeedTest(
            plugin: plugin,
            exp: exp,
            context: context,
            iterationsCount: iterationsCount,
          );
          print('$iterationsCount operations $exp in $res');
        }
      },
    );
  });

  group('Test calculations speed with enough variables', () {
    final context =
        DivVariableContext(current: {'a': 1, 'b': 2, 'c': 3, 'd': 4});
    const exp = '@{a + b + c + d}';
    final plugin = NativeDivExpressionResolver();
    testWidgets(
      'Get calculations speed for 500 a + b + c + d operations',
      (WidgetTester tester) async {
        const iterationsCount = 500;

        await plugin.clearVariables();
        for (int i = 0; i < 5; i++) {
          final res = await pluginSpeedTest(
            plugin: plugin,
            exp: exp,
            context: context,
            iterationsCount: iterationsCount,
          );
          print('$iterationsCount operations $exp in $res');
        }
      },
    );
    testWidgets(
      'Get calculations speed for 1000 a + b + c + d operations',
      (WidgetTester tester) async {
        const iterationsCount = 1000;

        await plugin.clearVariables();
        for (int i = 0; i < 5; i++) {
          final res = await pluginSpeedTest(
            plugin: plugin,
            exp: exp,
            context: context,
            iterationsCount: iterationsCount,
          );
          print('$iterationsCount operations $exp in $res');
        }
      },
    );
  });

  group('Test calculations speed with a lot of variables', () {
    const alph = 'abcdefghijklmnopqrstuvwxyz';
    final context = DivVariableContext(current: {
      for (int i = 0; i < 100; i++)
        alph[i % alph.length] * (1 + i ~/ alph.length): i,
    });
    const exp = '@{a + b + c + d}';
    final plugin = NativeDivExpressionResolver();
    testWidgets(
      'Get calculations speed for 500 a + b + c + d operations',
      (WidgetTester tester) async {
        const iterationsCount = 500;

        await plugin.clearVariables();
        for (int i = 0; i < 5; i++) {
          final res = await pluginSpeedTest(
            plugin: plugin,
            exp: exp,
            context: context,
            iterationsCount: iterationsCount,
          );
          print('$iterationsCount operations $exp in $res');
        }
      },
    );
    testWidgets(
      'Get calculations speed for 1000 a + b + c + d operations',
      (WidgetTester tester) async {
        const iterationsCount = 1000;

        await plugin.clearVariables();
        for (int i = 0; i < 5; i++) {
          final res = await pluginSpeedTest(
            plugin: plugin,
            exp: exp,
            context: context,
            iterationsCount: iterationsCount,
          );
          print('$iterationsCount operations $exp in $res');
        }
      },
    );
  });
}
