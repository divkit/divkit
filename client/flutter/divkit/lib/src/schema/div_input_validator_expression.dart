// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// [Calculated expression](https://divkit.tech/docs/en/concepts/expressions) validator.
class DivInputValidatorExpression extends Resolvable with EquatableMixin {
  const DivInputValidatorExpression({
    this.allowEmpty = const ValueExpression(false),
    required this.condition,
    required this.labelId,
    required this.variable,
  });

  static const type = "expression";

  /// Determines whether the empty field value is valid.
  // default value: false
  final Expression<bool> allowEmpty;

  /// [Calculated expression](https://divkit.tech/docs/en/concepts/expressions) used as a value validity condition.
  final Expression<bool> condition;

  /// ID of the text element containing the error message. The message will also be used for providing access.
  final Expression<String> labelId;

  /// The name of the variable that stores the calculation results.
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

  static DivInputValidatorExpression? fromJson(
    Map<String, dynamic>? json,
  ) {
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

  @override
  DivInputValidatorExpression resolve(DivVariableContext context) {
    allowEmpty.resolve(context);
    condition.resolve(context);
    labelId.resolve(context);
    return this;
  }
}
