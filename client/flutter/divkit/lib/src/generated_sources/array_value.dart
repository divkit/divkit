// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class ArrayValue with EquatableMixin {
  const ArrayValue({
    required this.value,
  });

  static const type = "array";

  final Expression<List<dynamic>> value;

  @override
  List<Object?> get props => [
        value,
      ];

  ArrayValue copyWith({
    Expression<List<dynamic>>? value,
  }) =>
      ArrayValue(
        value: value ?? this.value,
      );

  static ArrayValue? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return ArrayValue(
      value: safeParseListExpr(
        json['value'],
      )!,
    );
  }
}
