// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_typed_value.dart';

class DivActionArrayInsertValue with EquatableMixin {
  const DivActionArrayInsertValue({
    this.index,
    required this.value,
    required this.variableName,
  });

  static const type = "array_insert_value";

  final Expression<int>? index;

  final DivTypedValue value;

  final Expression<String> variableName;

  @override
  List<Object?> get props => [
        index,
        value,
        variableName,
      ];

  DivActionArrayInsertValue copyWith({
    Expression<int>? Function()? index,
    DivTypedValue? value,
    Expression<String>? variableName,
  }) =>
      DivActionArrayInsertValue(
        index: index != null ? index.call() : this.index,
        value: value ?? this.value,
        variableName: variableName ?? this.variableName,
      );

  static DivActionArrayInsertValue? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionArrayInsertValue(
        index: safeParseIntExpr(
          json['index'],
        ),
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
}
