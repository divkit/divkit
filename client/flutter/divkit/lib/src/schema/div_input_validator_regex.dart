// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivInputValidatorRegex extends Preloadable with EquatableMixin {
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

  DivInputValidatorRegex copyWith({
    Expression<bool>? allowEmpty,
    Expression<String>? labelId,
    Expression<String>? pattern,
    String? variable,
  }) =>
      DivInputValidatorRegex(
        allowEmpty: allowEmpty ?? this.allowEmpty,
        labelId: labelId ?? this.labelId,
        pattern: pattern ?? this.pattern,
        variable: variable ?? this.variable,
      );

  static DivInputValidatorRegex? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
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
    } catch (e) {
      return null;
    }
  }

  static Future<DivInputValidatorRegex?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivInputValidatorRegex(
        allowEmpty: (await safeParseBoolExprAsync(
          json['allow_empty'],
          fallback: false,
        ))!,
        labelId: (await safeParseStrExprAsync(
          json['label_id']?.toString(),
        ))!,
        pattern: (await safeParseStrExprAsync(
          json['pattern']?.toString(),
        ))!,
        variable: (await safeParseStrAsync(
          json['variable']?.toString(),
        ))!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {
    try {
      await allowEmpty.preload(context);
      await labelId.preload(context);
      await pattern.preload(context);
    } catch (e) {
      return;
    }
  }
}
