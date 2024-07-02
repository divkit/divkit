// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

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

  DivInputValidatorBase copyWith({
    Expression<bool>? allowEmpty,
    Expression<String>? Function()? labelId,
    String? Function()? variable,
  }) =>
      DivInputValidatorBase(
        allowEmpty: allowEmpty ?? this.allowEmpty,
        labelId: labelId != null ? labelId.call() : this.labelId,
        variable: variable != null ? variable.call() : this.variable,
      );

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
