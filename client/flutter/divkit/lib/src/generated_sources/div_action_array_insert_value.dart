// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_typed_value.dart';

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

  static DivActionArrayInsertValue? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
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
  }
}
