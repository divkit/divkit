// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_typed_value.dart';

class DivActionSetVariable with EquatableMixin {
  const DivActionSetVariable({
    required this.value,
    required this.variableName,
  });

  static const type = "set_variable";

  final DivTypedValue value;

  final Expression<String> variableName;

  @override
  List<Object?> get props => [
        value,
        variableName,
      ];

  DivActionSetVariable copyWith({
    DivTypedValue? value,
    Expression<String>? variableName,
  }) =>
      DivActionSetVariable(
        value: value ?? this.value,
        variableName: variableName ?? this.variableName,
      );

  static DivActionSetVariable? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivActionSetVariable(
      value: safeParseObj(
        DivTypedValue.fromJson(json['value']),
      )!,
      variableName: safeParseStrExpr(
        json['variable_name']?.toString(),
      )!,
    );
  }
}
