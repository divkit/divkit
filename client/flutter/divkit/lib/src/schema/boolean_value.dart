// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class BooleanValue extends Resolvable with EquatableMixin {
  const BooleanValue({
    required this.value,
  });

  static const type = "boolean";
  final Expression<bool> value;

  @override
  List<Object?> get props => [
        value,
      ];

  BooleanValue copyWith({
    Expression<bool>? value,
  }) =>
      BooleanValue(
        value: value ?? this.value,
      );

  static BooleanValue? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return BooleanValue(
        value: safeParseBoolExpr(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  BooleanValue resolve(DivVariableContext context) {
    value.resolve(context);
    return this;
  }
}
