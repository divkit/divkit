// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class BooleanValue with EquatableMixin {
  const BooleanValue({
    required this.value,
  });

  static const type = "boolean";

  final Expression<bool> value;

  @override
  List<Object?> get props => [
        value,
      ];

  static BooleanValue? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return BooleanValue(
      value: safeParseBoolExpr(
        json['value'],
      )!,
    );
  }
}
