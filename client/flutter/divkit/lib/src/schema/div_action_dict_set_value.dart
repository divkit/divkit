// Generated code. Do not modify.

import 'package:divkit/src/schema/div_typed_value.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Sets value in dictionary for the given key. Removes key if the value is not defined.
class DivActionDictSetValue extends Preloadable with EquatableMixin {
  const DivActionDictSetValue({
    required this.key,
    this.value,
    required this.variableName,
  });

  static const type = "dict_set_value";
  final Expression<String> key;
  final DivTypedValue? value;
  final Expression<String> variableName;

  @override
  List<Object?> get props => [
        key,
        value,
        variableName,
      ];

  DivActionDictSetValue copyWith({
    Expression<String>? key,
    DivTypedValue? Function()? value,
    Expression<String>? variableName,
  }) =>
      DivActionDictSetValue(
        key: key ?? this.key,
        value: value != null ? value.call() : this.value,
        variableName: variableName ?? this.variableName,
      );

  static DivActionDictSetValue? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionDictSetValue(
        key: safeParseStrExpr(
          json['key']?.toString(),
        )!,
        value: safeParseObj(
          DivTypedValue.fromJson(json['value']),
        ),
        variableName: safeParseStrExpr(
          json['variable_name']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivActionDictSetValue?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivActionDictSetValue(
        key: (await safeParseStrExprAsync(
          json['key']?.toString(),
        ))!,
        value: await safeParseObjAsync(
          DivTypedValue.fromJson(json['value']),
        ),
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
      await key.preload(context);
      await value?.preload(context);
      await variableName.preload(context);
    } catch (e) {
      return;
    }
  }
}
