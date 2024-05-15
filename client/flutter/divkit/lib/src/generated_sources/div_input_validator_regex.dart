// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class DivInputValidatorRegex with EquatableMixin {
  const DivInputValidatorRegex({
    this.allowEmpty = const ValueExpression(false),
    required this.labelId,
    required this.pattern,
    required this.variable,
  });

  static const type = "regex";
  // default value: false
  final Expression<bool> allowEmpty;

  final Expression<String> labelId;

  final Expression<String> pattern;

  final String variable;

  @override
  List<Object?> get props => [
        allowEmpty,
        labelId,
        pattern,
        variable,
      ];

  static DivInputValidatorRegex? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivInputValidatorRegex(
      allowEmpty: safeParseBoolExpr(
        json['allow_empty'],
        fallback: false,
      )!,
      labelId: safeParseStrExpr(
        json['label_id']?.toString(),
      )!,
      pattern: safeParseStrExpr(
        json['pattern']?.toString(),
      )!,
      variable: safeParseStr(
        json['variable']?.toString(),
      )!,
    );
  }
}
