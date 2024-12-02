// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

class DivInputValidatorBase extends Resolvable with EquatableMixin {
  const DivInputValidatorBase({
    this.allowEmpty = const ValueExpression(false),
    this.labelId,
    this.variable,
  });

  /// Determines whether the empty field value is valid.
  // default value: false
  final Expression<bool> allowEmpty;

  /// ID of the text element containing the error message. The message will also be used for providing access.
  final Expression<String>? labelId;

  /// The name of the variable that stores the calculation results.
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

  static DivInputValidatorBase? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivInputValidatorBase(
        allowEmpty: reqVProp<bool>(
          safeParseBoolExpr(
            json['allow_empty'],
            fallback: false,
          ),
          name: 'allow_empty',
        ),
        labelId: safeParseStrExpr(
          json['label_id'],
        ),
        variable: safeParseStr(
          json['variable'],
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivInputValidatorBase resolve(DivVariableContext context) {
    allowEmpty.resolve(context);
    labelId?.resolve(context);
    return this;
  }
}
