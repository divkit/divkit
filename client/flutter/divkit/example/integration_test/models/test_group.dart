import 'package:equatable/equatable.dart';

import 'expression_test_case.dart';

class TestGroup with EquatableMixin {
  final List<ExpressionTestCase>? cases;

  const TestGroup({
    this.cases,
  });

  factory TestGroup.fromJson(Map<String, Object?> json) => TestGroup(
        cases: (json['cases'] as List<dynamic>?)
            ?.map((e) => ExpressionTestCase.fromJson(e as Map<String, dynamic>))
            .toList(),
      );

  @override
  List<Object?> get props => [cases];
}
