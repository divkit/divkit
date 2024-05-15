// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class DivInputValidatorBase with EquatableMixin {
  const DivInputValidatorBase({
    this.allowEmpty = const ValueExpression(false),
    this.labelId,
    this.variable,
  });

  // default value: false
  final Expression<bool> allowEmpty;

  final Expression<String>? labelId;

  final String? variable;

  @override
  List<Object?> get props => [
        allowEmpty,
        labelId,
        variable,
      ];

  static DivInputValidatorBase? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivInputValidatorBase(
      allowEmpty: safeParseBoolExpr(
        json['allow_empty'],
        fallback: false,
      )!,
      labelId: safeParseStrExpr(
        json['label_id']?.toString(),
      ),
      variable: safeParseStr(
        json['variable']?.toString(),
      ),
    );
  }
}
