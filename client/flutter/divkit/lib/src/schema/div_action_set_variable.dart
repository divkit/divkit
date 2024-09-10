// Generated code. Do not modify.

import 'package:divkit/src/schema/div_typed_value.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Assigns a value to the variable
class DivActionSetVariable extends Preloadable with EquatableMixin {
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

  static DivActionSetVariable? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionSetVariable(
        value: safeParseObj(
          DivTypedValue.fromJson(json['value']),
        )!,
        variableName: safeParseStrExpr(
          json['variable_name']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivActionSetVariable?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivActionSetVariable(
        value: (await safeParseObjAsync(
          DivTypedValue.fromJson(json['value']),
        ))!,
        variableName: (await safeParseStrExprAsync(
          json['variable_name']?.toString(),
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
      await value.preload(context);
      await variableName.preload(context);
    } catch (e) {
      return;
    }
  }
}
