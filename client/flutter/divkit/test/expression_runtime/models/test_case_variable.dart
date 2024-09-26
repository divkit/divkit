import 'package:equatable/equatable.dart';

class TestCaseVariable with EquatableMixin {
  final String variableName;
  final String variableType;
  final dynamic value;

  const TestCaseVariable({
    required this.variableName,
    required this.variableType,
    this.value,
  });

  factory TestCaseVariable.fromJson(Map<String, Object?> json) =>
      TestCaseVariable(
        variableName: json['name'] as String,
        variableType: json['type'] as String,
        value: json['value'],
      );

  @override
  List<Object?> get props => [
        variableName,
        variableType,
        value,
      ];
}
