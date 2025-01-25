// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// [Calculated expression](https://divkit.tech/docs/en/concepts/expressions) validator.
class DivInputValidatorExpression with EquatableMixin {
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
        allowEmpty: reqVProp<bool>(
          safeParseBoolExpr(
            json['allow_empty'],
            fallback: false,
          ),
          name: 'allow_empty',
        ),
        condition: reqVProp<bool>(
          safeParseBoolExpr(
            json['condition'],
          ),
          name: 'condition',
        ),
        labelId: reqVProp<String>(
          safeParseStrExpr(
            json['label_id'],
          ),
          name: 'label_id',
        ),
        variable: reqProp<String>(
          safeParseStr(
            json['variable'],
          ),
          name: 'variable',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
