// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Filter based on [calculated expressions](https://divkit.tech/docs/en/concepts/expressions).
class DivInputFilterExpression extends Resolvable with EquatableMixin {
  const DivInputFilterExpression({
    required this.condition,
  });

  static const type = "expression";

  /// [Calculated expression](https://divkit.tech/docs/en/concepts/expressions) used to verify the validity of the value.
  final Expression<bool> condition;

  @override
  List<Object?> get props => [
        condition,
      ];

  DivInputFilterExpression copyWith({
    Expression<bool>? condition,
  }) =>
      DivInputFilterExpression(
        condition: condition ?? this.condition,
      );

  static DivInputFilterExpression? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivInputFilterExpression(
        condition: safeParseBoolExpr(
          json['condition'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  DivInputFilterExpression resolve(DivVariableContext context) {
    condition.resolve(context);
    return this;
  }
}
