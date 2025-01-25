import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/runtime/core.dart';
import 'package:flutter_test/flutter_test.dart';

import 'models/expression_test_case.dart';
import 'test_loader.dart';

const testDataPathBaseDir = 'test_data/expression_test_data';

final testDataPaths = [testDataPathBaseDir];

bool strictMode = false;

void main() async {
  for (final testDataPath in testDataPaths) {
    final tests = await loadTestData(testDataPath);

    print('Running tests...');
    print('All groups: ${tests.length}');
    for (var k in tests.keys) {
      if ({'functions_datetime_format'}.contains(k)) {
        print("Skip unsupported group: $k");
        continue;
      }

      group(k, () {
        for (ExpressionTestCase test in tests[k]?.cases ?? []) {
          if ({
            '@{string.var.toString()}',
            '@{string.var.toString()}',
            '@{setDay(setMonth(parseUnixTime(0), 2), -1)}',
            'variable name contains dot symbol',
          }.contains(test.caseName)) {
            print("Skip unsupported test: ${test.caseName}");
            continue;
          }

          testWidgets(
            test.caseName,
            (tester) async {
              final expression = test.expression;
              final variables = test.testVariables;
              var rv = test.result.value;
              var rt = test.result.resultType;

              try {
                final context = variables != null
                    ? {
                        for (var entry in variables)
                          entry.variableName:
                              safeParseNamed(entry.variableType)(entry.value),
                      }
                    : <String, dynamic>{};

                Object res =
                    runtime.execute(parser.parse(expression).value, context);
                if (rt == 'color') {
                  rv = parseColor(rv);
                } else if (rt == 'url') {
                  rv = parseUrl(rv);
                } else if (rt == 'datetime') {
                  rv = parseDate(rv);
                }
                print('Expecting: $rv [$rt]');
                print("Result: $res [${res.runtimeType}]");
                expect(res, rv);
              } catch (e) {
                print('Expecting: $rv [${rv.runtimeType} ($rt)]');
                print("Result: $e [${e.runtimeType}]");
                expect('error', rt);
                if (rv == '' || !strictMode) {
                  expect(true, true);
                } else {
                  expect(e.toString(), _ShadowExpressionMatcher(rv));
                }
              }
            },
          );
        }
      });
    }
  }
}

class _ShadowExpressionMatcher extends Matcher {
  final String expected;

  const _ShadowExpressionMatcher(this.expected);

  @override
  Description describe(Description description) => description;

  @override
  bool matches(item, Map matchState) {
    return expected
        .replaceFirst(RegExp(r'\[.*?\]'), '')
        .contains(item.replaceFirst(RegExp(r'\[.*?\]'), ''));
  }
}
