// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class DivActionArrayRemoveValue with EquatableMixin {
  const DivActionArrayRemoveValue({
    required this.index,
    required this.variableName,
  });

  static const type = "array_remove_value";

  final Expression<int> index;

  final Expression<String> variableName;

  @override
  List<Object?> get props => [
        index,
        variableName,
      ];

  DivActionArrayRemoveValue copyWith({
    Expression<int>? index,
    Expression<String>? variableName,
  }) =>
      DivActionArrayRemoveValue(
        index: index ?? this.index,
        variableName: variableName ?? this.variableName,
      );

  static DivActionArrayRemoveValue? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivActionArrayRemoveValue(
      index: safeParseIntExpr(
        json['index'],
      )!,
      variableName: safeParseStrExpr(
        json['variable_name']?.toString(),
      )!,
    );
  }
}
