import 'dart:convert';

import 'package:divkit/divkit.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';

import 'models/expression_test_case.dart';
import 'models/test_group.dart';

Future<Map<String, TestGroup>> loadTestData() async {
  final Map<String, dynamic> manifestMap = json.decode(
    await rootBundle.loadString('AssetManifest.json'),
  )..removeWhere(
      (key, value) =>
          (!key.startsWith('assets/test_data/expression_test_data')),
    );
  print('Tests cases: [${manifestMap.length} files]');
  Map<String, TestGroup> testCases = {};
  for (var path in manifestMap.keys) {
    final testCaseJson = await jsonDecode(
      await rootBundle.loadString(
        path.replaceAll('../', ''),
      ),
    );
    final name = path.split('/').last.split('.').first;
    testCases[name] = TestGroup.fromJson(testCaseJson);
  }
  return testCases;
}

Future<void> main() async {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();
  final data = await loadTestData();

  for (var k in data.keys) {
    group(k, () {
      for (ExpressionTestCase test in data[k]?.cases ?? []) {
        final platforms = test.availablePlatforms;

        if (platforms != null &&
            (!platforms.contains('ios') || !platforms.contains('android'))) {
          print("Skip ${test.caseName} -> not both [iOS, Android]");
        } else {
          testWidgets(
            test.caseName,
            (tester) async {
              final variables = test.testVariables;
              final plugin = DefaultDivExpressionResolver();
              await plugin.clearVariables();

              try {
                Object? res = await plugin.resolve(
                  ResolvableExpression(
                    test.expression,
                    parse: (raw) => safeParseNamed(test.result.resultType)(raw),
                    fallback: null,
                  ),
                  context: DivVariableContext(
                      current: variables != null
                          ? {
                              for (var entry in variables)
                                entry.variableName: safeParseNamed(
                                    entry.variableType)(entry.value)
                            }
                          : {}),
                );

                final rv = test.result.value;
                final rt = test.result.resultType;
                print('Expecting: $rv [$rt]');

                switch (rt) {
                  case 'number':
                    final numb = (res as double);
                    final rn = (rv as num).toDouble();
                    if (numb == rn) {
                      print("Result: $numb [equals? ${numb == rn}]");
                      expect(numb, rn);
                    } else {
                      final diff = 0.000001;
                      final eq = numb < rn + diff && numb > rn - diff;
                      print("Result: $numb [equals? $eq]");
                      expect(eq, true);
                    }
                    break;
                  case 'url':
                    final url = safeParseUri(rv);
                    print("Result: $res [equals? ${res == url}]");
                    expect(res, url);
                    break;
                  case 'color':
                    final color = safeParseColor(rv);
                    print("Result: $res [equals? ${res == color}]");
                    expect(res, color);
                    break;
                  default:
                    print("Result: $res [equals? ${res == rv}]");
                    expect(res, rv);
                }
              } on PlatformException catch (e) {
                print(
                    'Expecting: ${test.result.value} [${test.result.resultType}]');
                print("Result: ${e.message}");
                expect('error', test.result.resultType);
                expect(e.message?.contains(test.result.value), true);
              }
            },
          );
        }
      }
    });
  }
}
