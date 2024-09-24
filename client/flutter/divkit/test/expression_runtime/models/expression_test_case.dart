import 'package:equatable/equatable.dart';

import 'test_case_result.dart';
import 'test_case_variable.dart';

class ExpressionTestCase with EquatableMixin {
  final String caseName;
  final String expression;
  final TestCaseResult result;
  final List<TestCaseVariable>? testVariables;
  final List<String>? availablePlatforms;
  final Map<String, dynamic>? unsupportedPlatforms;

  const ExpressionTestCase({
    required this.caseName,
    required this.expression,
    required this.result,
    this.testVariables,
    this.availablePlatforms,
    this.unsupportedPlatforms,
  });

  factory ExpressionTestCase.fromJson(Map<String, Object?> json) {
    return ExpressionTestCase(
      caseName: json['name'] as String? ?? json['expression'] as String,
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
      unsupportedPlatforms:
          json['unsupported_platforms'] as Map<String, dynamic>?,
    );
  }

  @override
  List<Object?> get props => [
        caseName,
        expression,
        result,
        testVariables,
        availablePlatforms,
      ];
}
