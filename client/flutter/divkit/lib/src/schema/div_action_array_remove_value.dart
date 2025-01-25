// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Deletes a value from the array
class DivActionArrayRemoveValue extends Resolvable with EquatableMixin {
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

  static DivActionArrayRemoveValue? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionArrayRemoveValue(
        index: safeParseIntExpr(
          json['index'],
        )!,
        variableName: safeParseStrExpr(
          json['variable_name']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  DivActionArrayRemoveValue resolve(DivVariableContext context) {
    index.resolve(context);
    variableName.resolve(context);
    return this;
  }
}
