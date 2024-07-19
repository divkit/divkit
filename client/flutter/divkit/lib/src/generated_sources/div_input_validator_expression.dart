// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class DivInputValidatorExpression with EquatableMixin {
  const DivInputValidatorExpression({
    this.allowEmpty = const ValueExpression(false),
    required this.condition,
    required this.labelId,
    required this.variable,
  });

  static const type = "expression";
  // default value: false
  final Expression<bool> allowEmpty;

  final Expression<bool> condition;

  final Expression<String> labelId;

  final String variable;

  @override
  List<Object?> get props => [
        allowEmpty,
        condition,
        labelId,
        variable,
      ];

  DivInputValidatorExpression copyWith({
    Expression<bool>? allowEmpty,
    Expression<bool>? condition,
    Expression<String>? labelId,
    String? variable,
  }) =>
      DivInputValidatorExpression(
        allowEmpty: allowEmpty ?? this.allowEmpty,
        condition: condition ?? this.condition,
        labelId: labelId ?? this.labelId,
        variable: variable ?? this.variable,
      );

  static DivInputValidatorExpression? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      return DivInputValidatorExpression(
        allowEmpty: safeParseBoolExpr(
          json['allow_empty'],
          fallback: false,
        )!,
        condition: safeParseBoolExpr(
          json['condition'],
        )!,
        labelId: safeParseStrExpr(
          json['label_id']?.toString(),
        )!,
        variable: safeParseStr(
          json['variable']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }
}
