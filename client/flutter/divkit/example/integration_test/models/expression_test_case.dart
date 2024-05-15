import 'package:equatable/equatable.dart';

import 'test_case_result.dart';
import 'test_case_variable.dart';

class ExpressionTestCase with EquatableMixin {
  final String caseName;
  final String expression;
  final TestCaseResult result;
  final List<TestCaseVariable>? testVariables;
  final List<String>? availablePlatforms;

  const ExpressionTestCase({
    required this.caseName,
    required this.expression,
    required this.result,
    this.testVariables,
    this.availablePlatforms,
  });

  factory ExpressionTestCase.fromJson(Map<String, Object?> json) =>
      ExpressionTestCase(
        caseName: json['name'] as String,
        expression: json['expression'] as String,
        result: TestCaseResult.fromJson(
          json['expected'] as Map<String, dynamic>,
        ),
        testVariables: (json['variables'] as List<dynamic>?)
            ?.map((e) => TestCaseVariable.fromJson(e as Map<String, dynamic>))
            .toList(),
        availablePlatforms: (json['platforms'] as List<dynamic>?)
            ?.map((e) => e as String)
            .toList(),
      );

  @override
  List<Object?> get props => [
        caseName,
        expression,
        result,
        testVariables,
        availablePlatforms,
      ];
}
