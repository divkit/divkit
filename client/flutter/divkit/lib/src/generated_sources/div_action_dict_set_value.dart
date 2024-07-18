// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_typed_value.dart';

class DivActionDictSetValue with EquatableMixin {
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

  static DivActionDictSetValue? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
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
  }
}
