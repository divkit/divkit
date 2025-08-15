import 'package:equatable/equatable.dart';

class TestCaseResult with EquatableMixin {
  final String resultType;
  final dynamic value;

  const TestCaseResult({
    required this.resultType,
    this.value,
  });

  factory TestCaseResult.fromJson(Map<String, Object?> json) => TestCaseResult(
        resultType: json['type'] as String,
        value: json['value'],
      );

  @override
  List<Object?> get props => [
        resultType,
        value,
      ];
}
