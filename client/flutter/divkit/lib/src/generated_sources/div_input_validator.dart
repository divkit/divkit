// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'div_input_validator_expression.dart';
import 'div_input_validator_regex.dart';

class DivInputValidator with EquatableMixin {
  const DivInputValidator(Object value) : _value = value;

  final Object _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  Object get value {
    final value = _value;
    if (value is DivInputValidatorExpression) {
      return value;
    }
    if (value is DivInputValidatorRegex) {
      return value;
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivInputValidator");
  }

  T map<T>({
    required T Function(DivInputValidatorExpression)
        divInputValidatorExpression,
    required T Function(DivInputValidatorRegex) divInputValidatorRegex,
  }) {
    final value = _value;
    if (value is DivInputValidatorExpression) {
      return divInputValidatorExpression(value);
    }
    if (value is DivInputValidatorRegex) {
      return divInputValidatorRegex(value);
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivInputValidator");
  }

  T maybeMap<T>({
    T Function(DivInputValidatorExpression)? divInputValidatorExpression,
    T Function(DivInputValidatorRegex)? divInputValidatorRegex,
    required T Function() orElse,
  }) {
    final value = _value;
    if (value is DivInputValidatorExpression &&
        divInputValidatorExpression != null) {
      return divInputValidatorExpression(value);
    }
    if (value is DivInputValidatorRegex && divInputValidatorRegex != null) {
      return divInputValidatorRegex(value);
    }
    return orElse();
  }

  const DivInputValidator.divInputValidatorExpression(
    DivInputValidatorExpression value,
  ) : _value = value;

  const DivInputValidator.divInputValidatorRegex(
    DivInputValidatorRegex value,
  ) : _value = value;

  static DivInputValidator? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivInputValidatorRegex.type:
        return DivInputValidator(DivInputValidatorRegex.fromJson(json)!);
      case DivInputValidatorExpression.type:
        return DivInputValidator(DivInputValidatorExpression.fromJson(json)!);
    }
    return null;
  }
}
